package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by bj
 * on 18-1-10.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();
    }
}
