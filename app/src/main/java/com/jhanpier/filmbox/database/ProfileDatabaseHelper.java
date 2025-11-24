package com.jhanpier.filmbox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jhanpier.filmbox.R;

public class ProfileDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "profiles.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_PROFILES = "profiles";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_AVATAR = "avatar";

    public ProfileDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PROFILES + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_AVATAR + " INTEGER)";
        db.execSQL(CREATE_TABLE);

        // Agregamos un perfil por defecto
        db.execSQL("INSERT INTO " + TABLE_PROFILES +
                " ("+COL_NAME+", "+COL_AVATAR+") VALUES ('Profile 1', "+ R.drawable.profile_avatar3+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);
    }
}
