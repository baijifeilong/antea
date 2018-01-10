package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by bj
 * on 18-1-10.
 */

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setupToolbar();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Password password = databaseHelper.getDefaultPassword();
        String trigger = PreferenceManager.getDefaultSharedPreferences(this).getString("trigger", getString(R.string.the_default_trigger));
        try {
            ((Button) findViewById(R.id.test_text_button)).setText(Utils.encryptString(getString(R.string.test_text), password.value));
            ((TextView) findViewById(R.id.test_encryption_text_view)).setText(
                    String.format(getString(R.string.test_encryption), trigger));
        } catch (Utils.EncryptionException e) {
            e.printStackTrace();
            Toast.makeText(this, String.format(getString(R.string.error_occurred), e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }
}
