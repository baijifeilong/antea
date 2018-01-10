package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by bj
 * on 18-1-9.
 */

public class HelpActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setupToolbar();

        TextView txtHelp = findViewById(R.id.text_help);
        txtHelp.setText(Html.fromHtml(getString(R.string.app_description)));
        txtHelp.setMovementMethod(new LinkMovementMethod());
    }
}
