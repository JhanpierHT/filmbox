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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS videos");
        db.execSQL("DROP TABLE IF EXISTS movies_fav");
        onCreate(db);
    }

    // ---------------------------------------------------
    // INSERTAR UNA PELÍCULA
    // ---------------------------------------------------
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

    // ---------------------------------------------------
    // ELIMINAR TODAS LAS PELÍCULAS
    // ---------------------------------------------------
    public void clearMovies() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("videos", null, null);
    }

    // ---------------------------------------------------
    // OBTENER TODAS LAS PELÍCULAS
    // ---------------------------------------------------
    // ---------------------------------------------------
// OBTENER TODAS LAS PELÍCULAS
// ---------------------------------------------------
    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM videos", null);

        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie(
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("authors")),
                        cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        0 // progreso por defecto
                );

                list.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }


    public boolean isMovieFavorite(String title) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT 1 FROM movies_fav WHERE title = ?",
                new String[]{title}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public void toggleFavorite(Movie movie) {
        if (isFavorite(movie)) {
            removeFavorite(movie);
        } else {
            addFavorite(movie);
        }
    }

    public void addFavorite(Movie movie) {
        if (isFavorite(movie)) return;

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

    public List<Movie> getContinueWatchingMovies() {
        return new ArrayList<>();
    }

    public List<Movie> getAllFavorites() {
        List<Movie> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM movies_fav", null);

        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie(
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("authors")),
                        cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        0
                );

                list.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

}
