package io.github.baijifeilong.antea;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BaiJiFeiLong@gmail.com
 * on 2017/10/19 13:44
 */

public class PasswordGeneratorFragment extends Fragment {

    Spinner passwordLengthSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_password_generator, container, false);
        passwordLengthSpinner = view.findViewById(R.id.password_length_spinner);
        passwordLengthSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, generateNumberList()));
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
            ((CheckBox) view.findViewById(id)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generatePassword();
                }
            });
        }

        view.findViewById(R.id.copy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String password = ((TextView) view.findViewById(R.id.generated_password_text_view)).getText().toString();
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", password));
                Toast.makeText(getContext(), getString(R.string.generated_password_copied), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

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
        if (((CheckBox) getView().findViewById(R.id.uppercase_letters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.UPPERCASE_ENGLISH_LETTERS);
        }
        if (((CheckBox) getView().findViewById(R.id.lowercase_letters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.LOWERCASE_ENGLISH_LETTERS);
        }
        if (((CheckBox) getView().findViewById(R.id.arabic_numbers_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.ARABIC_NUMBERS);
        }
        if (((CheckBox) getView().findViewById(R.id.special_characters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.SPECIAL_CHARACTERS);
        }
        if (((CheckBox) getView().findViewById(R.id.chinese_characters_check_box)).isChecked()) {
            stringBuilder.append(RandomUtils.CHINESE_CHARACTERS_3500);
        }

        if (stringBuilder.length() > 0) {
            ((TextView) getView().findViewById(R.id.generated_password_text_view)).setText(RandomUtils.randomString(Integer.parseInt(passwordLengthSpinner.getSelectedItem().toString()), stringBuilder.toString()));
        }
    }
}
