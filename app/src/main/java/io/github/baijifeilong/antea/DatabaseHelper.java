package io.github.baijifeilong.antea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BaiJiFeiLong@gmail.com
 * on 2017/10/21 14:32
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_PASSWORD_TABLE = "CREATE TABLE password (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, password TEXT NOT NULL, created_at INTEGER NOT NULL, CHECK (title <> '' and password <> '') )";
    private static final String CREATE_CONFIG_TABLE = "CREATE TABLE config (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL, value TEXT NOT NULL, created_at INTEGER NOT NULL)";
    private static final String CREATE_DEFAULT_PASSWORD = String.format("INSERT INTO password (_id, title, password, created_at) values (1, 'default', '123', %s)", System.currentTimeMillis());
    private static final String SET_DEFAULT_PASSWORD = String.format("INSERT INTO config (_id, key, value, created_at) values (1, 'CURRENT_PASSWORD_ID', '1', %s)", System.currentTimeMillis());

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PASSWORD_TABLE);
        db.execSQL(CREATE_CONFIG_TABLE);
        db.execSQL(CREATE_DEFAULT_PASSWORD);
        db.execSQL(SET_DEFAULT_PASSWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
