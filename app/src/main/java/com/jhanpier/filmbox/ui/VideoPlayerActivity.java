package com.jhanpier.filmbox.ui;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.utils.InsecureSSLSocketFactory; // Asumo que esta clase es necesaria para tu servidor Render

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;

@OptIn(markerClass = UnstableApi.class)
public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageButton btnFullscreen;
    private ImageButton btnQuality;

    // Vistas de Información
    private TextView tvDescription;
    private TextView tvMovieTitle;
    private TextView tvMovieYear;
    private TextView tvMovieAuthors;
    private TextView tvExpandDescription;

    private boolean isFullscreen = false;
    private DefaultTrackSelector trackSelector;

    // Variables de datos (se inicializan en onCreate, pero se usan en initializePlayer)
    private String videoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 1. Configuración de Inicio: Inicia en vista normal (vertical)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // 2. Recepción y Asignación de Datos (se hace aquí para que estén disponibles en el ciclo de vida)
        videoUrl = getIntent().getStringExtra("videoUrl") != null ? getIntent().getStringExtra("videoUrl") : "";
        final String description = getIntent().getStringExtra("description") != null ? getIntent().getStringExtra("description") : "Sin sinopsis disponible.";
        final String title = getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "Título Desconocido";
        final String year = getIntent().getStringExtra("year") != null ? getIntent().getStringExtra("year") : "N/A";
        final String authors = getIntent().getStringExtra("authors") != null ? getIntent().getStringExtra("authors") : "Director: Desconocido";

        // 3. Inicialización de Vistas
        playerView = findViewById(R.id.playerView);
        btnFullscreen = findViewById(R.id.btnFullscreen);
        btnQuality = findViewById(R.id.btnQuality);

        tvDescription = findViewById(R.id.tvDescription);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieYear = findViewById(R.id.tvMovieYear);
        tvMovieAuthors = findViewById(R.id.tvMovieAuthors);
        tvExpandDescription = findViewById(R.id.tvExpandDescription);

        tvMovieTitle.setText(title);
        tvMovieYear.setText("(" + year + ")");
        tvMovieAuthors.setText("Director: " + authors);
        tvDescription.setText(description);

        // 4. Lógica de Expansión/Contracción de Descripción
        if (tvExpandDescription != null) {
            tvExpandDescription.setOnClickListener(v -> {
                if (tvDescription.getMaxLines() == 4) {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    tvExpandDescription.setText(R.string.mostrar_menos);
                } else {
                    tvDescription.setMaxLines(4);
                    tvExpandDescription.setText(R.string.mostrar_mas);
                }
            });
        }

        // 5. Inicializar el botón de pantalla completa
        btnFullscreen.setImageResource(R.drawable.ic_fullscreen);

        // 6. Listeners
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        btnQuality.setOnClickListener(v -> showQualitySelector());

        // 7. Validación de URL
        if (videoUrl.trim().isEmpty()) {
            Toast.makeText(this, "Video no disponible", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 🌟 NOTA: initializePlayer() se movió a onStart()/onResume() para mejor gestión del ciclo de vida
    }

    // --- SECCIÓN DE CONTROL UX ---

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;

        // Obtener parámetros del PlayerView
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) playerView.getLayoutParams();

        // Referencia al contenedor de detalles
        View detailsContainer = findViewById(R.id.svDetailsContainer);

        // 1. Controlar la orientación y visibilidad
        if (isFullscreen) {
            // --- MODO PANTALLA COMPLETA ---

            // A. Ajuste de PlayerView: Eliminar 16:9 y usar MATCH_PARENT
            params.dimensionRatio = null;
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;

            // Eliminar restricciones inferiores
            params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;

            playerView.setLayoutParams(params);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // B. Modo inmersivo (Ocultar barras del sistema)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            // C. Ocultar el contenedor de detalles
            if (detailsContainer != null) {
                detailsContainer.setVisibility(View.GONE);
            }
            btnQuality.setVisibility(View.GONE);

        } else {
            // --- MODO VENTANA NORMAL (Vertical) ---

            // A. Restaurar PlayerView: Usar 0dp y restaurar 16:9
            params.dimensionRatio = "16:9";
            params.width = 0;
            params.height = 0;

            // Restaurar restricción inferior
            if (detailsContainer != null) {
                params.bottomToTop = detailsContainer.getId();
            }

            playerView.setLayoutParams(params);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            // B. Mostrar barras del sistema
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            // C. Mostrar el contenedor de detalles
            if (detailsContainer != null) {
                detailsContainer.setVisibility(View.VISIBLE);
            }
            btnQuality.setVisibility(View.VISIBLE);
        }

        // 2. Actualizar el ícono del botón
        btnFullscreen.setImageResource(isFullscreen ? R.drawable.ic_fullscreen_exit : R.drawable.ic_fullscreen);
    }

    // --- SECCIÓN DE REPRODUCTOR (EXOPLAYER/MEDIA3) ---

    private void initializePlayer(String url) {
        if (player == null) {
            trackSelector = new DefaultTrackSelector(this);

            player = new ExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build();

            playerView.setPlayer(player);
            playerView.requestFocus();

            // Configuración del OkHttpClient con SSL inseguro para el servidor (si es necesario)
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(InsecureSSLSocketFactory.sslContext.getSocketFactory(), InsecureSSLSocketFactory.trustManager)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            OkHttpDataSource.Factory dataSourceFactory = new OkHttpDataSource.Factory(okHttpClient);
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(url)));

            player.setMediaSource(mediaSource);
            player.prepare();
        }
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    private void showQualitySelector() {
        if (player == null) return;

        Tracks tracks = player.getCurrentTracks();
        List<Tracks.Group> videoTrackGroups = new ArrayList<>();

        for (Tracks.Group trackGroup : tracks.getGroups()) {
            if (trackGroup.getType() == C.TRACK_TYPE_VIDEO && trackGroup.isSupported()) {
                videoTrackGroups.add(trackGroup);
            }
        }

        if (videoTrackGroups.isEmpty()) {
            Toast.makeText(this, "No hay opciones de calidad disponibles (posiblemente video no adaptativo)", Toast.LENGTH_SHORT).show();
            return;
        }

        final Tracks.Group selectedGroup = videoTrackGroups.get(0);

        String[] qualityOptions = new String[selectedGroup.length + 1];
        qualityOptions[0] = "Automático";

        for (int i = 0; i < selectedGroup.length; i++) {
            Format format = selectedGroup.getTrackFormat(i);
            int height = format.height;
            int bitrate = format.bitrate;

            String label = height > 0 ? height + "p" : "Resolución desconocida";
            if (bitrate > 0) label += " (" + (bitrate / 1000) + " Kbps)";

            qualityOptions[i + 1] = label;
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar Calidad")
                .setItems(qualityOptions, (dialog, which) -> {
                    if (which == 0) {
                        trackSelector.setParameters(trackSelector.buildUponParameters().clearOverrides());
                    } else {
                        int trackIndex = which - 1;

                        TrackSelectionOverride override = new TrackSelectionOverride(
                                selectedGroup.getMediaTrackGroup(),
                                Collections.singletonList(trackIndex)
                        );

                        trackSelector.setParameters(trackSelector.buildUponParameters()
                                .setOverrideForType(override)
                                .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, false));
                    }
                    Toast.makeText(this, qualityOptions[which] + " seleccionada", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // --- GESTIÓN DEL CICLO DE VIDA (OPTIMIZADA) ---

    @Override
    protected void onStart() {
        super.onStart();
        // Inicializar el reproductor aquí si la API es 24+ (Android 7.0+)
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            if (!videoUrl.trim().isEmpty()) {
                initializePlayer(videoUrl);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reanudar o inicializar si la API es inferior a 24
        if (android.os.Build.VERSION.SDK_INT < 24 || player == null) {
            if (!videoUrl.trim().isEmpty()) {
                initializePlayer(videoUrl);
            }
        }
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausar si la API es 24+
        if (android.os.Build.VERSION.SDK_INT >= 24 && player != null) {
            player.setPlayWhenReady(false);
        }
        // Liberar si la API es inferior a 24
        if (android.os.Build.VERSION.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Liberar si la API es 24+
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            toggleFullscreen(); // Sale de pantalla completa
        } else {
            super.onBackPressed();
        }
    }
}