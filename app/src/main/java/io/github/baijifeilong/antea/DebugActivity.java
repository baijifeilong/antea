package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/9/21 21:03
 */

public class DebugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        findViewById(R.id.foo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = PreferenceManager.getDefaultSharedPreferences(DebugActivity.this).getString("password", "123");
                Toast.makeText(DebugActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
