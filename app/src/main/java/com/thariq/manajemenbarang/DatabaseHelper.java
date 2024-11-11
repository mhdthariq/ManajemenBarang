package com.thariq.manajemenbarang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "storage.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to trigger onUpgrade
    private static final String TABLE_USERS = "users";
    private static final String TABLE_STORAGE = "storage";
    private static DatabaseHelper instance;

    // Singleton pattern to prevent multiple instances
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STORAGE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, kodeBarang TEXT, namaBarang TEXT, merk TEXT, hargaBarang INTEGER, jumlahStock INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORAGE);
        onCreate(db);
    }

    // Insert user
    public boolean insertUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return insert(TABLE_USERS, values);
    }

    // Check login credentials
    public boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?", new String[]{username, password})) {
            return cursor.getCount() > 0;
        }
    }

    // CRUD for Storage
    public boolean insertBarang(String kodeBarang, String namaBarang, String merk, int hargaBarang, int jumlahStock) {
        ContentValues values = new ContentValues();
        values.put("kodeBarang", kodeBarang);
        values.put("namaBarang", namaBarang);
        values.put("merk", merk);
        values.put("hargaBarang", hargaBarang);
        values.put("jumlahStock", jumlahStock);
        return insert(TABLE_STORAGE, values);
    }

    public Cursor getAllBarang() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_STORAGE, null);
    }

    public boolean updateBarang(int id, String kodeBarang, String namaBarang, String merk, int hargaBarang, int jumlahStock) {
        ContentValues values = new ContentValues();
        values.put("kodeBarang", kodeBarang);
        values.put("namaBarang", namaBarang);
        values.put("merk", merk);
        values.put("hargaBarang", hargaBarang);
        values.put("jumlahStock", jumlahStock);
        return update(TABLE_STORAGE, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public boolean deleteBarang(int id) {
        return delete(TABLE_STORAGE, "_id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Helper methods for CRUD operations
    private boolean insert(String table, ContentValues values) {
        long result = getWritableDatabase().insert(table, null, values);
        return result != -1;
    }

    private boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int rowsAffected = getWritableDatabase().update(table, values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    private int delete(String table, String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(table, whereClause, whereArgs);
    }
}