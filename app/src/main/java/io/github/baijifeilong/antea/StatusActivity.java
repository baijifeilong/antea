package io.github.baijifeilong.antea;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bj
 * on 18-1-9.
 */

public class StatusActivity extends BaseActivity {
    private Switch serviceSwitch;
    private PasswordAdapter passwordAdapter;
    private DatabaseHelper databaseHelper;
    private ListView lstPassword;
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setupToolbar();
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        serviceSwitch = findViewById(R.id.switch_service);
        serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Switch) v).setChecked(!((Switch) v).isChecked());
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        databaseHelper = new DatabaseHelper(this);

        lstPassword = findViewById(R.id.list_password);

        List<Password> passwordList = databaseHelper.getPasswordList();
        passwordAdapter = new PasswordAdapter(this, passwordList);
        lstPassword.setAdapter(passwordAdapter);
        refreshPasswordList();
        lstPassword.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Password password = passwordAdapter.getItem(position);
                databaseHelper.setDefaultPassword(password);
                refreshPasswordList();
            }
        });

        findViewById(android.R.id.content).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                StatusActivity.this.finish();
                startActivity(new Intent(StatusActivity.this, MessageActivity.class));
            }

            public void onSwipeBottom() {
                StatusActivity.this.finish();
                startActivity(new Intent(StatusActivity.this, MessageActivity.class));
            }
        });
    }

    private void refreshPasswordList() {
        List<Password> passwordList = databaseHelper.getPasswordList();
        passwordAdapter.clear();
        passwordAdapter.addAll(passwordList);
        passwordAdapter.notifyDataSetChanged();
        for (int i = 0; i < passwordList.size(); ++i) {
            Password password = passwordList.get(i);
            if (password.isDefault) {
                lstPassword.setItemChecked(i, true);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceSwitch.setChecked(isAccessibilityEnabled(BuildConfig.APPLICATION_ID + "/.AnteaService"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_create_password) {
            String password = RandomUtils.randomString(10, RandomUtils.LETTERS_AND_NUMBERS);
            databaseHelper.insertPassword("Password " + password, password);
            refreshPasswordList();
        } else if (item.getItemId() == R.id.menu_edit_password) {
            Password password = passwordAdapter.getItem(lstPassword.getCheckedItemPosition());
            startActivityForResult(new Intent(this, PasswordEditActivity.class).putExtra("password", password), 0);
        } else if (item.getItemId() == R.id.menu_delete_password) {
            if (lstPassword.getCount() <= 1) {
                Toast.makeText(this, "Please keep at least one password", Toast.LENGTH_SHORT).show();
            } else {
                int index = lstPassword.getCheckedItemPosition();
                Password password = passwordAdapter.getItem(index);
                assert password != null;
                databaseHelper.deletePassword(password);
                refreshPasswordList();
            }
        } else if (item.getItemId() == R.id.menu_copy_to_clipboard) {
            Password password = passwordAdapter.getItem(lstPassword.getCheckedItemPosition());
            assert password != null;
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, password.value));
            String msg = String.format(getString(R.string.copied_to_clipboard), password.value);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            refreshPasswordList();
        }
    }

    private static final class PasswordAdapter extends ArrayAdapter<Password> {
        PasswordAdapter(@NonNull Context context, @NonNull List<Password> passwordList) {
            super(context, android.R.layout.simple_list_item_single_choice, passwordList);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            Password password = getItem(position);
            assert password != null;
            textView.setText(password.name + ": " + password.value);
            return view;
        }
    }
}
