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

public class AnteaService extends AccessibilityService {

    private static final String TAG = "AnteaService";

    private Toast toast;

    private String lastCopiedMessage = null;
    private ClipData oldClipData = null;
    private long lastDoubleClickedTime = 0;
    private boolean doubleClicked = false;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
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

                    ClipData clip = ClipData.newPlainText("label", encryptedString);
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData oldClipData = clipboardManager.getPrimaryClip();
                    clipboardManager.setPrimaryClip(clip);
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    clipboardManager.setPrimaryClip(oldClipData);
                } catch (Utils.EncryptionException e) {
                    e.printStackTrace();
                    toast("加密失败：" + e.getLocalizedMessage());
                }
            }
        } else {
            text = text.trim();
            if (text.length() > 0 && text.length() % 32 == 0 && text.charAt(0) <= 'F' && text.charAt(text.length() - 1) <= 'F') {
                try {
                    String decryptedString = Utils.decryptString(text, password);
                    if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                        Log.i(TAG, "Clicked");
                        Log.i(TAG, String.valueOf(System.currentTimeMillis() - lastDoubleClickedTime));
                        if (!doubleClicked) {
                            toast(decryptedString);
                        }
                        doubleClicked = false;
                    } else {
                        Log.i(TAG, "Double clicked");
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        if (decryptedString.equals(lastCopiedMessage) && System.currentTimeMillis() - lastDoubleClickedTime < 10000) {
                            clipboardManager.setPrimaryClip(oldClipData);
                            toast(String.format("剪贴板已还原为:\n%s", clipDataToString(oldClipData)));
                            lastCopiedMessage = null;
                        } else {
                            oldClipData = clipboardManager.getPrimaryClip();
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", decryptedString));
                            toast(String.format("已复制到剪贴板,10秒内长按可撤销:\n%s", decryptedString));
                            lastCopiedMessage = decryptedString;
                        }
                        lastDoubleClickedTime = System.currentTimeMillis();
                        doubleClicked = true;
                    }
                } catch (Utils.DecryptionException e) {
                    if (Utils.isHex(text)) {
                        e.printStackTrace();
                        toast("解密失败,密码或密文错误: " + e.getLocalizedMessage());
                    }
                }
            } else if (PreferenceManager.getDefaultSharedPreferences(AnteaService.this).getBoolean("debug", false)) {
                toast(String.format("当前点击的文本: %s", text));
            }
        }
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
}
