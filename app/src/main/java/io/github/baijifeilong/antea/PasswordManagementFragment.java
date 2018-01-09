package io.github.baijifeilong.antea;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiJiFeiLong@gmail.com
 * on 2017/10/21 15:11
 */

class Password {
    long id;
    String title;
    String password;
}

public class PasswordManagementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_password_management, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.passwords_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "antea.db", null, 1);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("password", null, null, null, null, null, null);
        final List<Password> passwords = new ArrayList<>();
        while (cursor.moveToNext()) {
            Password password = new Password();
            password.id = cursor.getLong(cursor.getColumnIndex("_id"));
            password.title = cursor.getString(cursor.getColumnIndex("title"));
            password.password = cursor.getString(cursor.getColumnIndex("password"));
            passwords.add(password);
        }
        cursor.close();

        final PasswordAdapter passwordAdapter = new PasswordAdapter(getContext(), passwords, db);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(passwordAdapter);

        view.findViewById(R.id.create_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = new Password();
                password.title = "Password " + (passwords.size() + 1);
                password.password = "123";
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", password.title);
                contentValues.put("password", password.password);
                contentValues.put("created_at", System.currentTimeMillis());
                password.id = db.insertOrThrow("password", null, contentValues);
                passwords.add(password);
                passwordAdapter.expandedPosition = passwords.size() - 1;
                passwordAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}

class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {
    private Context context;
    private List<Password> passwordList;
    int expandedPosition = -1;
    private SQLiteDatabase db;

    PasswordAdapter(Context context, List<Password> passwordList, SQLiteDatabase db) {
        this.context = context;
        this.passwordList = passwordList;
        this.db = db;
    }

    @Override
    public PasswordViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final PasswordViewHolder viewHolder = new PasswordViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_password, parent, false));
        viewHolder.passwordSummaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedPosition = expandedPosition == viewHolder.getAdapterPosition() ? -1 : viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        viewHolder.savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = passwordList.get(viewHolder.getAdapterPosition());
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", viewHolder.titleEditText.getText().toString());
                contentValues.put("password", viewHolder.passwordEditText.getText().toString());
                PasswordAdapter.this.db.update("password", contentValues, "_id = ?", new String[]{String.valueOf(password.id)});
                password.title = contentValues.getAsString("title");
                password.password = contentValues.getAsString("password");
                expandedPosition = -1;
                notifyItemChanged(viewHolder.getAdapterPosition());
                Toast.makeText(context, "Password saved", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.deletePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = passwordList.get(viewHolder.getAdapterPosition());
                PasswordAdapter.this.db.delete("password", "_id = ?", new String[]{String.valueOf(password.id)});
                passwordList.remove(password);
                expandedPosition = -1;
                notifyDataSetChanged();
                Toast.makeText(context, "Password deleted", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.copyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = passwordList.get(viewHolder.getAdapterPosition());
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", password.password));
                expandedPosition = -1;
                notifyItemChanged(viewHolder.getAdapterPosition());
                Toast.makeText(context, "Password copied", Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PasswordViewHolder holder, int position) {
        Password password = passwordList.get(position);
        holder.passwordSummaryTextView.setText(String.format("%s: %s", password.title, password.password));
        holder.titleEditText.setText(password.title);
        holder.passwordEditText.setText(password.password);
        holder.passwordEditLayout.setVisibility(position == expandedPosition ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    class PasswordViewHolder extends RecyclerView.ViewHolder {
        ViewGroup passwordSummaryLayout;
        ViewGroup passwordEditLayout;
        TextView passwordSummaryTextView;
        EditText titleEditText;
        EditText passwordEditText;
        Button copyPasswordButton;
        Button savePasswordButton;
        Button deletePasswordButton;

        PasswordViewHolder(View itemView) {
            super(itemView);
            passwordSummaryLayout = itemView.findViewById(R.id.password_summary_layout);
            passwordEditLayout = itemView.findViewById(R.id.password_edit_layout);
            passwordSummaryTextView = itemView.findViewById(R.id.password_summary_text_view);
            titleEditText = itemView.findViewById(R.id.title_edit_text);
            passwordEditText = itemView.findViewById(R.id.password_edit_text);
            copyPasswordButton = itemView.findViewById(R.id.copy_password_button);
            savePasswordButton = itemView.findViewById(R.id.save_password_button);
            deletePasswordButton = itemView.findViewById(R.id.delete_password_button);
        }
    }
}
