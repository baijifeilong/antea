package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

/**
 * Created by bj
 * on 18-1-10.
 */

public class PasswordEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtName;
    private EditText edtValue;
    private Password password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_password_edit);
        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        edtName = findViewById(R.id.edit_name);
        edtValue = findViewById(R.id.edit_value);

        password = (Password) getIntent().getSerializableExtra("password");
        edtName.setText(password.name);
        edtValue.setText(password.value);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) {
            password.name = edtName.getText().toString();
            password.value = edtValue.getText().toString();
            new DatabaseHelper(this).updatePassword(password);
            setResult(RESULT_OK);
            finish();
        } else if (v.getId() == R.id.button_cancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
