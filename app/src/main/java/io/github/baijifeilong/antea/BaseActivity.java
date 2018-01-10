package io.github.baijifeilong.antea;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by bj
 * on 18-1-9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    protected boolean isAccessibilityEnabled(String id) {
        AccessibilityManager am = (AccessibilityManager) this.getSystemService(Context.ACCESSIBILITY_SERVICE);
        assert am != null;
        for (AccessibilityServiceInfo service : am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected Toolbar setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
