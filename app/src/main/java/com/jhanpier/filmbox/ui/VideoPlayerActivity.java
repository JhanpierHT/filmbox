package com.jhanpier.filmbox.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.media3.datasource.okhttp.OkHttpDataSource;

import com.jhanpier.filmbox.utils.InsecureSSLSocketFactory;
import com.jhanpier.filmbox.R;

import okhttp3.OkHttpClient;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageButton btnFullscreen;
    private TextView tvDescription;

    private boolean isFullscreen = false;
    private boolean playerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);
        btnFullscreen = findViewById(R.id.btnFullscreen);
        tvDescription = findViewById(R.id.tvDescription);

        final String videoUrl = getIntent().getStringExtra("videoUrl") != null ? getIntent().getStringExtra("videoUrl") : "";
        final String description = getIntent().getStringExtra("description") != null ? getIntent().getStringExtra("description") : "Sin descripción";
        tvDescription.setText(description);

        btnFullscreen.setOnClickListener(v -> toggleFullscreen());

        // Simple validation: no coroutines — si la URL es no vacía inicializamos
        if (videoUrl.trim().isEmpty()) {
            Toast.makeText(this, "Video no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initializePlayer(videoUrl);
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        setRequestedOrientation(isFullscreen ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isFullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            tvDescription.setVisibility(View.GONE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String videoUrl) {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(InsecureSSLSocketFactory.sslContext.getSocketFactory(), InsecureSSLSocketFactory.trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .build();

        OkHttpDataSource.Factory dataSourceFactory = new OkHttpDataSource.Factory(okHttpClient);
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        playerInitialized = true;
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) { toggleFullscreen(); } else { super.onBackPressed(); }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playerInitialized && player != null) {
            player.release();
            playerView.setPlayer(null);
            playerInitialized = false;
        }
    }
}
