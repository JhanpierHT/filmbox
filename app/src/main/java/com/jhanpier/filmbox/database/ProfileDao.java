package com.jhanpier.filmbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jhanpier.filmbox.model.Profile;

import java.util.ArrayList;
import java.util.List;

public class ProfileDao {

    private final ProfileDatabaseHelper dbHelper;

    public ProfileDao(Context context) {
        dbHelper = new ProfileDatabaseHelper(context);
    }

    public long insert(Profile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProfileDatabaseHelper.COL_NAME, profile.getName());
        values.put(ProfileDatabaseHelper.COL_AVATAR, profile.getAvatarResId());
        return db.insert(ProfileDatabaseHelper.TABLE_PROFILES, null, values);
    }

    public List<Profile> getAllProfiles() {
        List<Profile> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ProfileDatabaseHelper.TABLE_PROFILES,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Profile profile = new Profile();
                profile.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COL_ID)));
                profile.setName(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COL_NAME)));
                profile.setAvatarResId(cursor.getInt(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COL_AVATAR)));
                list.add(profile);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public void delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ProfileDatabaseHelper.TABLE_PROFILES, "id=?", new String[]{String.valueOf(id)});
    }

    public void update(Profile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProfileDatabaseHelper.COL_NAME, profile.getName());
        values.put(ProfileDatabaseHelper.COL_AVATAR, profile.getAvatarResId());

        db.update(ProfileDatabaseHelper.TABLE_PROFILES, values,
                "id=?", new String[]{String.valueOf(profile.getId())});
    }
}
