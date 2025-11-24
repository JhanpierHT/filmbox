package com.jhanpier.filmbox.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.ui.VideoPlayerActivity;
import com.jhanpier.filmbox.database.VideoDatabaseHelper;
import com.jhanpier.filmbox.model.Movie;

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
    private String currentCategory = "Todos";
    private static final int REQUEST_CODE_VOICE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        recyclerView = findViewById(R.id.rvVideoMovies);
        searchView = findViewById(R.id.searchView);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new VideoDatabaseHelper(this);

        setupBottomNavigation();
        setupVoiceSearch();

        insertInitialMoviesIfNeeded();
        allMovies = dbHelper.getAllMovies();
        setUpAdapter(allMovies);
        setupSearchView();
        setupSpinner();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_video);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) { startActivity(new Intent(this, HomeActivity.class)); return true; }
            else if (id == R.id.nav_video) return true;
            else if (id == R.id.nav_favorite) { startActivity(new Intent(this, FavoritesActivity.class)); return true; }
            else if (id == R.id.nav_profile) { startActivity(new Intent(this, ProfileActivity.class)); return true; }
            return false;
        });
    }

    private void insertInitialMoviesIfNeeded() {
        if (dbHelper.getAllMovies().isEmpty()) {

            List<Movie> initialMovies = new ArrayList<>();

            initialMovies.add(new Movie(
                    "The Walking Dead",
                    "2010",
                    "Un grupo de sobrevivientes lucha por vivir en un mundo devastado por zombis.",
                    "Frank Darabont",
                    "https://files.catbox.moe/k7svc0.mp4",
                    "https://files.catbox.moe/13j99u.jpg",
                    "Series"
            ));

            initialMovies.add(new Movie(
                    "Joker 2: Folie à Deux",
                    "2024",
                    "Arthur Fleck retorna en una historia musical oscura junto a Harley Quinn.",
                    "Todd Phillips",
                    "https://files.catbox.moe/md3jix.mp4",
                    "https://files.catbox.moe/div5pw.jpg",
                    "Películas"
            ));

            initialMovies.add(new Movie(
                    "Game of Thrones",
                    "2011",
                    "Nobles de Poniente luchan por el Trono de Hierro mientras fuerzas antiguas despiertan.",
                    "David Benioff & D.B. Weiss",
                    "https://files.catbox.moe/w306av.mp4",
                    "https://files.catbox.moe/tz8ho1.jpg",
                    "Series"
            ));

            initialMovies.add(new Movie(
                    "Gladiador 2",
                    "2024",
                    "La historia continúa con el legado de Máximo en una nueva era del Coliseo.",
                    "Ridley Scott",
                    "https://files.catbox.moe/h1nx1e.mp4",
                    "https://files.catbox.moe/dnhans.jpg",
                    "Películas"
            ));

            initialMovies.add(new Movie(
                    "The Witcher",
                    "2019",
                    "Geralt de Rivia, un cazador de monstruos, enfrenta su destino en un continente lleno de magia.",
                    "Lauren Schmidt Hissrich",
                    "https://files.catbox.moe/xoft8v.mp4",
                    "https://files.catbox.moe/7gxo45.jpg",
                    "Series"
            ));

            initialMovies.add(new Movie(
                    "Frankenstein",
                    "2025",
                    "Un científico desafía los límites de la naturaleza al crear vida artificial.",
                    "Guillermo del Toro",
                    "https://files.catbox.moe/umrlyh.mp4",
                    "https://files.catbox.moe/dkv09p.jpg",
                    "Películas"
            ));

            initialMovies.add(new Movie(
                    "Alienheart",
                    "2023",
                    "Una fuerza alienígena desconocida controla emociones humanas con efectos devastadores.",
                    "Roxanne Lister",
                    "https://files.catbox.moe/g141ta.mp4",
                    "https://files.catbox.moe/y532uz.jpg",
                    "Películas"
            ));

            initialMovies.add(new Movie(
                    "Zootopia 2",
                    "2025",
                    "Judy y Nick vuelven para resolver un caso que amenaza la paz de la ciudad.",
                    "Byron Howard",
                    "https://files.catbox.moe/k7yrbk.mp4",
                    "https://files.catbox.moe/8y6yst.jpg",
                    "Películas"
            ));

            // Insertar todas
            for (Movie m : initialMovies) {
                dbHelper.insertMovie(m);
            }
        }
    }


    private void setupSearchView() {
        SearchView sv = findViewById(R.id.searchView);
        android.widget.EditText searchEditText = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) { filterMovies(); return true; }
        });
    }

    private void setupSpinner() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                currentCategory = parent.getItemAtPosition(position).toString();
                setupVoiceSearch();
                filterMovies();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void filterMovies() {
        String query = searchView.getQuery().toString().toLowerCase(Locale.getDefault()).trim();
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : allMovies) {
            boolean matchesTitle = movie.getTitle().toLowerCase(Locale.getDefault()).contains(query);
            boolean matchesCategory = currentCategory.equals("Todos") || movie.getCategory().equals(currentCategory);
            if (matchesTitle && matchesCategory) filtered.add(movie);
        }
        movieAdapter.updateList(filtered);
    }

    private void setUpAdapter(List<Movie> movieList) {
        movieAdapter = new VideoAdapter(movieList,
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
                    startActivity(intent);
                });
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupVoiceSearch() {
        android.widget.ImageButton btnVoice = findViewById(R.id.btnVoiceSearch);
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
            java.util.ArrayList<String> result = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = (result != null && !result.isEmpty()) ? result.get(0) : "";
            SearchView sv = findViewById(R.id.searchView);
            sv.setQuery(recognizedText, true);
        }
    }
}
