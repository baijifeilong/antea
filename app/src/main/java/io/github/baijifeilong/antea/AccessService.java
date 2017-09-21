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
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/9/20 21:19
 */

public class AccessService extends AccessibilityService {

    private static final String TAG = "AccessService";


    private Toast toast;

    private void shortToast(String string) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);
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
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", "123");
        String trigger = PreferenceManager.getDefaultSharedPreferences(this).getString("trigger", "。。。。");

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (text.endsWith(trigger)) {
                text = text.substring(0, text.length() - trigger.length());

                String encryptedString;
                try {
                    encryptedString = Utils.encryptString(text, password);

                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                            AccessibilityNodeInfo.MOVEMENT_GRANULARITY_PAGE);
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

                    ClipData clip = ClipData.newPlainText("label", encryptedString);
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(clip);
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } catch (Utils.EncryptionException e) {
                    e.printStackTrace();
                    shortToast("加密失败：" + e.getLocalizedMessage());
                }
            }
        } else {
            if (text.length() > 0 && text.length() % 32 == 0 && text.charAt(0) <= 'F' && text.charAt(text.length() - 1) <= 'F') {
                try {
                    String decryptedString = Utils.decryptString(text, password);
                    shortToast(decryptedString);
                } catch (Utils.DecryptionException e) {
                    if (Utils.isHex(text)) {
                        e.printStackTrace();
                        shortToast("解密失败: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }
}
