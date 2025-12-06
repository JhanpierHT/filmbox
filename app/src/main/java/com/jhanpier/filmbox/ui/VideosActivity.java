package com.jhanpier.filmbox.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.database.VideoDatabaseHelper;
import com.jhanpier.filmbox.model.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter movieAdapter;
    private VideoDatabaseHelper dbHelper;
    private SearchView searchView;
    private Spinner spinnerCategory;

    private List<Movie> allMovies = new ArrayList<>();
    private String currentCategory = "Todos"; // Valor por defecto
    private static final int REQUEST_CODE_VOICE = 1;
    private boolean isSpinnerInitialLoad = true;

    private static final String MOVIES_URL = "https://filmbox-server.onrender.com/api/movies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        recyclerView = findViewById(R.id.rvVideoMovies);
        searchView = findViewById(R.id.searchView);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new VideoDatabaseHelper(this);

        // 🌟 CORRECCIÓN 1: Revisar si se pasó una categoría desde HomeActivity
        String categoryFromIntent = getIntent().getStringExtra("category");
        if (categoryFromIntent != null && !categoryFromIntent.isEmpty()) {
            currentCategory = categoryFromIntent;
        }

        setupBottomNavigation();
        setupVoiceSearch();

        // Cargar JSON del servidor (Tarea asíncrona)
        new LoadMoviesTask().execute(MOVIES_URL);

        setupSearchView();
        setupSpinner();
    }


    // ------------------- DESCARGA DE DATOS -------------------
    private class LoadMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... urls) {
            List<Movie> movies = new ArrayList<>();
            // ... (Lógica de descarga y parseo JSON, la dejamos igual) ...

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                JSONArray jsonArray = new JSONArray(result.toString());

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);

                    Movie movie = new Movie(
                            obj.getString("title"),
                            obj.getString("year"),
                            obj.getString("description"),
                            obj.getString("authors"),
                            obj.getString("videoUrl"),
                            obj.getString("imageUrl"),
                            obj.getString("category")
                    );

                    movies.add(movie);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            if (!movies.isEmpty()) {
                dbHelper.clearMovies();

                for (Movie m : movies) {
                    dbHelper.insertMovie(m);
                }
                // Cargar desde SQLite
                allMovies = dbHelper.getAllMovies();
            }

            allMovies = dbHelper.getAllMovies();
            setUpAdapter(allMovies);

            // 🌟 CORRECCIÓN 2: Aplicar el filtro después de cargar los datos
            filterMovies();

            // 🌟 CORRECCIÓN 3: Si venimos de una categoría específica, seleccionar
            // ese ítem en el Spinner para que visualmente esté correcto.
            selectSpinnerItem(currentCategory);
        }
    }

    // ------------------- LÓGICA DE FILTRADO -------------------

    /**
     * Busca y selecciona el ítem en el Spinner que coincide con la categoría dada.
     */
    private void selectSpinnerItem(String category) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(category)) {
                    spinnerCategory.setSelection(i);
                    // Establecer initialLoad a true DE NUEVO después de la selección manual
                    // para que el listener NO se dispare, ya que acabamos de filtrar.
                    isSpinnerInitialLoad = true;
                    break;
                }
            }
        }
    }


    private void setupSpinner() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {

                // Saltar el primer evento (que es la selección al iniciar/manual)
                if (isSpinnerInitialLoad) {
                    isSpinnerInitialLoad = false;
                    return;
                }

                // El usuario ha seleccionado un nuevo filtro.
                currentCategory = parent.getItemAtPosition(position).toString();
                filterMovies();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void filterMovies() {
        if (movieAdapter == null) {
            return;
        }

        String query = searchView.getQuery().toString().toLowerCase(Locale.getDefault()).trim();
        List<Movie> filtered = new ArrayList<>();

        for (Movie movie : allMovies) {
            boolean matchTitle = movie.getTitle().toLowerCase(Locale.getDefault()).contains(query);
            boolean matchCategory = currentCategory.equals("Todos") || movie.getCategory().equals(currentCategory);

            if (matchTitle && matchCategory) filtered.add(movie);
        }

        movieAdapter.updateList(filtered);
    }

    // ------------------- RESTO DE MÉTODOS -------------------

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // ... (Selección del ítem actual)
        bottomNavigationView.setSelectedItemId(R.id.nav_video);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (id == R.id.nav_video) {
                intent = new Intent(this, VideosActivity.class);
            } else if (id == R.id.nav_favorite) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            } else {
                return true; // Ya estamos en esta actividad o el ID no es de navegación.
            }

            if (intent != null) {
                // ✅ MANTENEMOS: CLEAR_TOP y NEW_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // ❌ ELIMINAR ESTA LÍNEA en todas las pestañas:
                // finish();

                return true;
            }
            return false;
        });
    }

    private void setupSearchView() {
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                filterMovies();
                return true;
            }
        });
    }

    private void setUpAdapter(List<Movie> movieList) {
        movieAdapter = new VideoAdapter(
                movieList,
                movie -> {
                    Intent intent = new Intent(this, MovieDetailActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("year", movie.getYear());
                    intent.putExtra("description", movie.getDescription());
                    intent.putExtra("authors", movie.getAuthors());
                    intent.putExtra("videoUrl", movie.getVideoUrl());
                    intent.putExtra("imageUrl", movie.getImageUrl());
                    startActivity(intent);
                },
                movie -> {
                    dbHelper.toggleFavorite(movie);
                    movieAdapter.notifyDataSetChanged();
                    Toast.makeText(this, movie.getTitle() + " favorito actualizado", Toast.LENGTH_SHORT).show();
                },
                movie -> dbHelper.isMovieFavorite(movie.getTitle()),
                movie -> {
                    Intent intent = new Intent(this, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl", movie.getVideoUrl());
                    intent.putExtra("description", movie.getDescription());
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("year", movie.getYear());
                    intent.putExtra("authors", movie.getAuthors());
                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(movieAdapter);
    }


    private void setupVoiceSearch() {
        ImageButton btnVoice = findViewById(R.id.btnVoiceSearch);
        btnVoice.setOnClickListener(v -> {
            Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Habla ahora...");

            try {
                startActivityForResult(intent, REQUEST_CODE_VOICE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Tu dispositivo no admite búsqueda por voz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_VOICE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = (result != null && !result.isEmpty()) ? result.get(0) : "";
            searchView.setQuery(recognizedText, true);
        }
    }
}