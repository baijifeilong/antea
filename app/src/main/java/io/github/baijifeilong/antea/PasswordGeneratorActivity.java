package io.github.baijifeilong.antea;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by bj
 * on 18-1-10.
 */

public class PasswordGeneratorActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);
        setupToolbar();

        passwordLengthSpinner = findViewById(R.id.password_length_spinner);
        passwordLengthSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, generateNumberList()));
        passwordLengthSpinner.setSelection(15);
        passwordLengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generatePassword();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        for (int id : new int[]{R.id.uppercase_letters_check_box, R.id.lowercase_letters_check_box, R.id.arabic_numbers_check_box, R.id.special_characters_check_box, R.id.chinese_characters_check_box}) {
            ((CheckBox) findViewById(id)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generatePassword();
                }
            });
        }

        findViewById(R.id.copy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String password = ((TextView) findViewById(R.id.generated_password_text_view)).getText().toString();
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", password));
                String msg = String.format(getString(R.string.copied_to_clipboard), password);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    Spinner passwordLengthSpinner;


    @Override
    public void onStart() {
        super.onStart();
        generatePassword();
    }

    private String[] generateNumberList() {
        String[] numbers = new String[128];
        for (int i = 0; i < 128; ++i) {
            numbers[i] = String.valueOf(i + 1);
        }
        return numbers;
    }

    private void generatePassword() {
        StringBuilder stringBuilder = new StringBuilder();
        if (((CheckBox) findViewById(R.id.uppercase_letters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.UPPERCASE_ENGLISH_LETTERS);
        }
        if (((CheckBox) findViewById(R.id.lowercase_letters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.LOWERCASE_ENGLISH_LETTERS);
        }
        if (((CheckBox) findViewById(R.id.arabic_numbers_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.ARABIC_NUMBERS);
        }
        if (((CheckBox) findViewById(R.id.special_characters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.SPECIAL_CHARACTERS);
        }
        if (((CheckBox) findViewById(R.id.chinese_characters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.CHINESE_CHARACTERS_3500);
        }

        if (stringBuilder.length() > 0) {
            ((TextView) findViewById(R.id.generated_password_text_view)).setText(RandomUtils.randomString(Integer.parseInt(passwordLengthSpinner.getSelectedItem().toString()), stringBuilder.toString()));
        }
    }
}
