package io.github.baijifeilong.antea;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiJiFeiLong@gmail.com
 * on 2017/10/21 14:32
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DROP_PASSWORD_TABLE = "DROP TABLE IF EXISTS password";
    private static final String CREATE_PASSWORD_TABLE = "CREATE TABLE password (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL UNIQUE, " +
            "value TEXT NOT NULL, " +
            "is_default INTEGER NOT NULL DEFAULT 0, " +
            "CHECK (name <> '' and value <> '') )";
    private static final String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS message";
    private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE message (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "time INTEGER NOT NULL, " +
            "content TEXT NOT NULL UNIQUE" +
            ")";

    DatabaseHelper(Context context) {
        super(context, "antea.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DROP_PASSWORD_TABLE);
        db.execSQL(CREATE_PASSWORD_TABLE);
        db.execSQL(DROP_MESSAGE_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL("INSERT INTO password(name, value, is_default) VALUES ('臣伏黎首', '123', 1)");
        db.execSQL("INSERT INTO password(name, value) VALUES ('愛育戎羌', '" +
                RandomUtils.randomString(10, RandomUtils.LETTERS_AND_NUMBERS) + "')");
        db.execSQL("INSERT INTO password(name, value) VALUES ('坐朝問道', '" +
                RandomUtils.randomString(15, RandomUtils.LETTERS_AND_NUMBERS) + "')");
        db.execSQL("INSERT INTO password(name, value) VALUES ('鳥官人皇', '" +
                RandomUtils.randomString(8, RandomUtils.CHINESE_CHARACTERS_3500) + "')");
        db.execSQL("INSERT INTO message(time, content) VALUES (" + System.currentTimeMillis() + ", '臣伏黎首')");
        db.execSQL("INSERT INTO message(time, content) VALUES (" + System.currentTimeMillis() + ", '愛育戎羌')");
        db.execSQL("INSERT INTO message(time, content) VALUES (" + System.currentTimeMillis() + ", '坐朝問道')");
        db.execSQL("INSERT INTO message(time, content) VALUES (" + System.currentTimeMillis() + ", '鳥官人皇')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    List<Password> getPasswordList() {
        Cursor cursor = getReadableDatabase().query("password", null, null, null, null, null, null);
        return cursorToPasswordList(cursor);
    }

    List<Password> getPasswordListForDecrypt() {
        Cursor cursor = getReadableDatabase().query("password", null, null, null, null, null, "is_default DESC");
        return cursorToPasswordList(cursor);
    }

    private List<Password> cursorToPasswordList(Cursor cursor) {
        List<Password> passwordList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
            boolean isDefault = cursor.getInt(cursor.getColumnIndexOrThrow("is_default")) == 1;
            passwordList.add(new Password(id, name, value, isDefault));
        }
        cursor.close();
        return passwordList;
    }

    void insertPassword(String name, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("value", value);
        getWritableDatabase().insertOrThrow("password", null, contentValues);
    }

    void deletePassword(Password password) {
        getWritableDatabase().delete("password", "_id = " + password.id, null);
        if (password.isDefault) {
            String firstRowCondition = "rowid = (SELECT MIN(rowid) FROM password)";
            ContentValues contentValues = new ContentValues();
            contentValues.put("is_default", 1);
            getWritableDatabase().update("password", contentValues, firstRowCondition, null);
        }
    }

    Password getDefaultPassword() {
        Cursor cursor = getReadableDatabase().query("password", null, "is_default = 1", null, null, null, null);
        return cursorToPasswordList(cursor).get(0);
    }

    void setDefaultPassword(Password password) {
        Password defaultPassword = getDefaultPassword();
        getWritableDatabase().execSQL("UPDATE password SET is_default = 0 where _id = " + defaultPassword.id);
        getWritableDatabase().execSQL("UPDATE password SET is_default = 1 where _id = " + password.id);
    }

    void updatePassword(Password password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", password.name);
        contentValues.put("value", password.value);
        contentValues.put("is_default", password.isDefault ? 1 : 0);
        getWritableDatabase().update("password", contentValues, "_id = " + password.id, null);
    }

    Cursor getMessageCursor() {
        return getWritableDatabase().query("message", null, null, null, null, null, "time DESC");
    }

    @SuppressLint("DefaultLocale")
    void insertOrRefreshMessage(String content) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT _id FROM message WHERE content = '%s'", content), null);
        if (cursor.moveToNext()) {
            long currentTime = System.currentTimeMillis();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            db.execSQL("UPDATE message SET time = " + currentTime + " WHERE _id = " + id);
            cursor.close();
        } else {
            cursor.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put("time", System.currentTimeMillis());
            contentValues.put("content", content);
            db.insertOrThrow("message", null, contentValues);
        }
    }

    void clearMessages() {
        getWritableDatabase().execSQL("DELETE FROM message WHERE _id > 4");
    }
}
