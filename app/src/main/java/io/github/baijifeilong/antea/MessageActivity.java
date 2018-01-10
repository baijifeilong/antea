package io.github.baijifeilong.antea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bj
 * on 18-1-10.
 */

public class MessageActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setupToolbar();

        ListView lstMessage = findViewById(R.id.list_message);
        databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getMessageCursor();
        messageAdapter = new MessageAdapter(this, cursor);
        lstMessage.setAdapter(messageAdapter);

        findViewById(android.R.id.content).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                MessageActivity.this.finish();
                startActivity(new Intent(MessageActivity.this, StatusActivity.class));
            }

            public void onSwipeBottom() {
                MessageActivity.this.finish();
                startActivity(new Intent(MessageActivity.this, StatusActivity.class));
            }
        });
    }


    private static final class MessageAdapter extends ResourceCursorAdapter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        MessageAdapter(Context context, Cursor cursor) {
            super(context, android.R.layout.simple_list_item_2, cursor, true);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setPadding(dpToPx(5), 0, dpToPx(5), 0);
            TextView txtContent = view.findViewById(android.R.id.text1);
            TextView txtTime = view.findViewById(android.R.id.text2);
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            Date date = new Date(time);
            txtContent.setText(content);
            txtTime.setText(dateFormat.format(date) + " [" + (cursor.getPosition() + 1) + "]");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMessageList();
    }

    private void refreshMessageList() {
        Cursor cursor = databaseHelper.getMessageCursor();
        messageAdapter.changeCursor(cursor);
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_messages) {
            databaseHelper.clearMessages();
            refreshMessageList();
            Toast.makeText(this, getString(R.string.message_clear_success), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
