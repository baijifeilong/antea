package io.github.baijifeilong.antea;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final List<Triple<Integer, Integer, Class>> MENUS = new ArrayList<Triple<Integer, Integer, Class>>() {{
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_status, R.drawable.ic_toys, StatusActivity.class));
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_message, R.drawable.ic_gesture, MessageActivity.class));
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_password_generator, R.drawable.ic_vpn_key, PasswordGeneratorActivity.class));
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_test, R.drawable.ic_bug_report, TestActivity.class));
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_settings, R.drawable.ic_settings, SettingsActivity.class));
        this.add(new Triple<Integer, Integer, Class>(R.string.activity_help, R.drawable.ic_help, HelpActivity.class));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);

        ListView lstMenu = findViewById(R.id.list_menu);
        final MenuAdapter menuAdapter = new MenuAdapter(this, MENUS);
        lstMenu.setAdapter(menuAdapter);
        lstMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //noinspection ConstantConditions
                startActivity(new Intent(MainActivity.this, menuAdapter.getItem(position).third));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == android.R.id.home || super.onOptionsItemSelected(item);
    }

    private static class MenuAdapter extends ArrayAdapter<Triple<Integer, Integer, Class>> {
        MenuAdapter(@NonNull Context context, List<Triple<Integer, Integer, Class>> menus) {
            super(context, R.layout.item_menu, menus);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Triple<Integer, Integer, Class> menu = getItem(position);
            assert menu != null;
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            Drawable drawable = getContext().getResources().getDrawable(menu.second);
            ColorFilter filter = new LightingColorFilter(Color.BLACK, Color.BLACK);
            drawable.setColorFilter(filter);
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            textView.setText(menu.first);
            return view;
        }
    }
}
