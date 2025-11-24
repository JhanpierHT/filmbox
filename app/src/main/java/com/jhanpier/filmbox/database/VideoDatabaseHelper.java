package com.jhanpier.filmbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jhanpier.filmbox.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class VideoDatabaseHelper extends SQLiteOpenHelper {

    public VideoDatabaseHelper(Context context) {
        super(context, "videos.db", null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE videos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "year TEXT," +
                        "description TEXT," +
                        "authors TEXT," +
                        "videoUrl TEXT," +
                        "imageUrl TEXT," +
                        "category TEXT)"
        );

        db.execSQL(
                "CREATE TABLE movies_fav (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "year TEXT," +
                        "description TEXT," +
                        "authors TEXT," +
                        "videoUrl TEXT," +
                        "imageUrl TEXT," +
                        "category TEXT)"
        );
    }

    public void resetDatabaseManual() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS videos");
        db.execSQL("DROP TABLE IF EXISTS movies_fav");
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS videos");
        db.execSQL("DROP TABLE IF EXISTS movies_fav");
        onCreate(db);
    }

    public long insertMovie(Movie m) {
        ContentValues cv = new ContentValues();
        cv.put("title", m.getTitle());
        cv.put("year", m.getYear());
        cv.put("description", m.getDescription());
        cv.put("authors", m.getAuthors());
        cv.put("videoUrl", m.getVideoUrl());
        cv.put("imageUrl", m.getImageUrl());
        cv.put("category", m.getCategory());

        return getWritableDatabase().insert("videos", null, cv);
    }

    public boolean isMovieFavorite(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM movies_fav WHERE title = ?", new String[]{title});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void toggleFavorite(Movie movie) {
        if (isMovieFavorite(movie.getTitle())) {
            getWritableDatabase().delete("movies_fav", "title = ?", new String[]{movie.getTitle()});
        } else {
            addFavorite(movie);
        }
    }

    public List<Movie> getAllMovies() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM videos", null);
        List<Movie> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie(
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("authors")),
                        cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category"))
                );
                list.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public void deleteAllMovies() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("movies", null, null);
        db.close();
    }

    public List<Movie> getAllFavorites() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM movies_fav", null);
        List<Movie> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie(
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("authors")),
                        cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category"))
                );
                list.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public void addFavorite(Movie movie) {
        if (!isFavorite(movie)) {
            ContentValues cv = new ContentValues();
            cv.put("title", movie.getTitle());
            cv.put("year", movie.getYear());
            cv.put("description", movie.getDescription());
            cv.put("authors", movie.getAuthors());
            cv.put("videoUrl", movie.getVideoUrl());
            cv.put("imageUrl", movie.getImageUrl());
            cv.put("category", movie.getCategory());

            getWritableDatabase().insert("movies_fav", null, cv);
        }
    }

    public void removeFavorite(Movie movie) {
        getWritableDatabase().delete(
                "movies_fav",
                "title=? AND year=?",
                new String[]{movie.getTitle(), movie.getYear()}
        );
    }

    public boolean isFavorite(Movie movie) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT 1 FROM movies_fav WHERE title=? AND year=?",
                new String[]{movie.getTitle(), movie.getYear()}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
