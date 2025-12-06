package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.adapters.CategoryAdapter;
// Se elimina la importación de ContinueAdapter
import com.jhanpier.filmbox.adapters.SliderAdapterHome;
import com.jhanpier.filmbox.adapters.TopAdapter;
import com.jhanpier.filmbox.database.VideoDatabaseHelper;
import com.jhanpier.filmbox.model.Movie;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class HomeActivity extends AppCompatActivity {

    // 🌟 URL Base de Contenido (Debe coincidir con tu servidor Render)
    private static final String CONTENT_BASE_URL = "https://filmbox-server.onrender.com";

    private ViewPager2 vpSlider;
    private LinearLayout llIndicators;
    // Se elimina rvContinue
    private RecyclerView rvTop3, rvCategories; // rvContinue eliminado

    private SliderAdapterHome sliderAdapter;

    // Se elimina la variable continueAdapter
    private int sliderPosition = 0;
    private Handler sliderHandler;

    private VideoDatabaseHelper db;
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new VideoDatabaseHelper(this);
        movies = db.getAllMovies();

        // Preprocesar URLs después de cargar de la base de datos local
        preprocessMovieUrls(movies);

        initViews();
        loadSlider();
        loadTop3();

        // Se elimina la llamada a loadContinueWatching()
        loadCategories();
        setupBottomNavigation();
    }

    /**
     * Itera sobre la lista de películas y asegura que videoUrl e imageUrl sean URLs absolutas.
     */
    private void preprocessMovieUrls(List<Movie> movieList) {
        if (movieList == null || CONTENT_BASE_URL.isEmpty()) return;

        for (Movie movie : movieList) {
            // Corregir videoUrl
            String videoUrl = movie.getVideoUrl();
            if (videoUrl != null && videoUrl.startsWith("/")) {
                movie.setVideoUrl(CONTENT_BASE_URL + videoUrl);
            }
            // Corregir imageUrl
            String imageUrl = movie.getImageUrl();
            if (imageUrl != null && imageUrl.startsWith("/")) {
                movie.setImageUrl(CONTENT_BASE_URL + imageUrl);
            }
        }
    }


    private void initViews() {
        vpSlider = findViewById(R.id.vpSlider);
        llIndicators = findViewById(R.id.llSliderIndicators);

        rvTop3 = findViewById(R.id.rvTop10); // Usando el ID definido
        rvTop3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Se elimina la inicialización de rvContinue
        // rvContinue = findViewById(R.id.rvContinue);
        // rvContinue.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    // ------------------- SLIDER -------------------
    private void loadSlider() {
        if (movies.isEmpty()) {
            Toast.makeText(this, "No hay películas cargadas.", Toast.LENGTH_LONG).show();
            return;
        }

        List<Movie> sliderMovies = movies.subList(0, Math.min(5, movies.size()));

        sliderAdapter = new SliderAdapterHome(sliderMovies, movie -> {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            sendMovieData(intent, movie);
            startActivity(intent);
        });

        vpSlider.setAdapter(sliderAdapter);
        addIndicators(sliderMovies.size());
        highlightIndicator(0);

        vpSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                sliderPosition = position;
                highlightIndicator(position);
            }
        });

        startAutoSlide(sliderMovies.size());
    }

    private void startAutoSlide(int size) {
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (size > 0) {
                    sliderPosition = (sliderPosition + 1) % size;
                    vpSlider.setCurrentItem(sliderPosition, true);
                    sliderHandler.postDelayed(this, 4000);
                }
            }
        }, 4000);
    }

    private void addIndicators(int count) {
        llIndicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            LinearLayout dot = new LinearLayout(this);
            dot.setBackgroundResource(R.drawable.indicator_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            llIndicators.addView(dot);
        }
    }

    private void highlightIndicator(int index) {
        for (int i = 0; i < llIndicators.getChildCount(); i++) {
            llIndicators.getChildAt(i)
                    .setBackgroundResource(i == index
                            ? R.drawable.indicator_active
                            : R.drawable.indicator_inactive);
        }
    }

    // ------------------- TOP 3 -------------------
    private void loadTop3() {
        // Aseguramos que haya al menos una película para evitar errores de subList.
        List<Movie> top3Movies = movies.subList(0, Math.min(3, movies.size()));

        TopAdapter adapter = new TopAdapter(this, top3Movies, movie -> {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            sendMovieData(intent, movie);
            startActivity(intent);
        });

        rvTop3.setAdapter(adapter);

    }

    // ---------------- CONTINUE WATCHING ----------------
    // Se elimina el método loadContinueWatching()

    // ---------------- CATEGORIES ----------------
    private void loadCategories() {
        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("Películas");
        categoriesList.add("Series");
        categoriesList.add("Documentales");

        CategoryAdapter adapter = new CategoryAdapter(categoriesList, category -> {
            Intent i = new Intent(this, VideosActivity.class);
            i.putExtra("category", category);
            startActivity(i);
        });

        rvCategories.setAdapter(adapter);
    }

    // ---------------- NAVEGACIÓN Y DATOS ----------------

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // ... (Selección del ítem actual)
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

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

    private void sendMovieData(Intent intent, Movie movie) {
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("year", movie.getYear());
        intent.putExtra("description", movie.getDescription());
        intent.putExtra("authors", movie.getAuthors());
        intent.putExtra("videoUrl", movie.getVideoUrl());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reiniciar el auto slide si la actividad vuelve al primer plano
        if (sliderAdapter != null) {
            startAutoSlide(sliderAdapter.getItemCount());
        }
    }

    @Override
    public void onBackPressed() {
        // Intercepta el botón Atrás SÓLO en la actividad principal (Home)
        new AlertDialog.Builder(this)
                .setTitle("Salir de FilmBox")
                .setMessage("¿Quieres salir de la aplicación?")
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    // Si el Login/Register fue cerrado correctamente (paso 2),
                    // super.onBackPressed() cerrará la aplicación.
                    super.onBackPressed();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // No hacer nada, quedarse en la Home.
                })
                .show();
    }
}