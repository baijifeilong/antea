package io.github.baijifeilong.antea;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/9/20 21:19
 */

public class AnteaService extends AccessibilityService implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String TAG = "AnteaService";
    public static final String CLIP_LABEL = "Antea";

    private Toast toast;

    private String lastCopiedMessage = null;
    private ClipData oldClipData = null;
    private long lastDoubleClickedTime = 0;
    private boolean doubleClicked = false;

    private DatabaseHelper databaseHelper;
    private ClipboardManager clipboardManager;

    private void shortToast(String string) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void longToast(String string) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_LONG);
        toast.show();
    }

    private void toast(String string) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("longToast", false)) {
            longToast(string);
        } else {
            shortToast(string);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);

        clipboardManager.addPrimaryClipChangedListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i(TAG, "onAccessibilityEvent");
        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();

        if (accessibilityEvent.getText().isEmpty()) {
            return;
        }

        String text = accessibilityEvent.getText().get(0).toString();
        Password password = databaseHelper.getDefaultPassword();
        String passwordValue = password.value;
        String trigger = PreferenceManager.getDefaultSharedPreferences(this).getString("trigger", getString(R.string.the_default_trigger));

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (text.endsWith(trigger)) {
                text = text.substring(0, text.length() - trigger.length());

                String encryptedString;
                try {
                    encryptedString = Utils.encryptString(text, passwordValue);

                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                            AccessibilityNodeInfo.MOVEMENT_GRANULARITY_PARAGRAPH);
                    arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                            true);

                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                            arguments);
                    for (int i = 0; i < text.length(); ++i) {
                        if (text.charAt(i) == '\n') {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                                    arguments);
                        }
                    }

                    ClipData clip = ClipData.newPlainText(CLIP_LABEL, encryptedString);
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    assert clipboardManager != null;
                    ClipData oldClipData = clipboardManager.getPrimaryClip();
                    clipboardManager.setPrimaryClip(clip);
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    clipboardManager.setPrimaryClip(oldClipData);
                    databaseHelper.insertOrRefreshMessage(text);
                    toast(String.format(getString(R.string.encrypted_by_password), password.name, password.value, text));
                } catch (Utils.EncryptionException e) {
                    e.printStackTrace();
                    toast(String.format(getString(R.string.encryption_failed), e.getMessage()));
                }
            }
        } else {
            doDecrypt(accessibilityEvent);
        }
    }

    void doDecrypt(AccessibilityEvent accessibilityEvent) {
        String text = accessibilityEvent.getText().get(0).toString();
        if (isEncryptedString(text)) {
            Pair<String, Password> decryptResult = decrypt(text);
            if (decryptResult == null) {
                toast(getString(R.string.decryption_failed));
                return;
            }
            String decryptedString = decryptResult.first;
            Password password = decryptResult.second;
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                Log.i(TAG, String.valueOf(System.currentTimeMillis() - lastDoubleClickedTime));
                if (!doubleClicked) {
                    databaseHelper.insertOrRefreshMessage(decryptedString);
                    toast(String.format(getString(R.string.decrypted_by_password), password.name, password.value, decryptedString));
                }
                doubleClicked = false;
            } else {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                if (decryptedString.equals(lastCopiedMessage) && System.currentTimeMillis() - lastDoubleClickedTime < 10000) {
                    clipboardManager.setPrimaryClip(oldClipData);
                    toast(String.format(getString(R.string.clipboard_restored), clipDataToString(oldClipData)));
                    lastCopiedMessage = null;
                } else {
                    oldClipData = clipboardManager.getPrimaryClip();
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, decryptedString));
                    toast(String.format(getString(R.string.decrypted_text_copied), decryptedString));
                    lastCopiedMessage = decryptedString;
                }
                lastDoubleClickedTime = System.currentTimeMillis();
                doubleClicked = true;
            }
        } else if (PreferenceManager.getDefaultSharedPreferences(AnteaService.this).getBoolean("debug", false)) {
            toast(String.format(getString(R.string.current_tapped_text), text));
        }
    }

    public Pair<String, Password> decrypt(String text) {
        List<Password> passwordList = databaseHelper.getPasswordListForDecrypt();
        for (Password password : passwordList) {
            try {
                return new Pair<>(Utils.decryptString(text, password.value), password);
            } catch (Utils.DecryptionException e) {
                Log.d("AnteaService", "Decrypt failed: " + e.getLocalizedMessage());
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }

    private static String clipDataToString(ClipData clipData) {
        try {
            return clipData.getItemAt(0).getText().toString();
        } catch (Exception e) {
            return clipData.toString();
        }
    }

    private boolean isEncryptedString(String text) {
        if (text != null && text.length() > 0 && text.length() % 32 == 0) {
            for (int i = 0; i < text.length(); ++i) {
                char ch = text.charAt(i);
                if (!(ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9')) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPrimaryClipChanged() {
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (!CLIP_LABEL.equals(clipData.getDescription().getLabel())) {
            String text = clipDataToString(clipData);
            if (isEncryptedString(text)) {
                Pair<String, Password> decryptResult = decrypt(text);
                if (decryptResult != null) {
                    Password password = decryptResult.second;
                    String decryptedString = decryptResult.first;
                    databaseHelper.insertOrRefreshMessage(decryptedString);
                    toast(String.format(getString(R.string.decrypted_by_password), password.name, password.value, decryptedString));
                } else {
                    toast(getString(R.string.decryption_failed));
                }
            }
        }
    }
}
