package com.jhanpier.filmbox.ui;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.jhanpier.filmbox.R;

public class MovieDetailActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Forzar horizontal igual que en Kotlin
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // PlayerView desde el layout
        playerView = findViewById(R.id.playerView);

        // URL enviada desde un intent
        String videoUrl = getIntent().getStringExtra("video_url");
        if (videoUrl == null) videoUrl = "";

        // Crear ExoPlayer (Media3)
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Cargar video
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);

        // Preparar y reproducir
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Liberar memoria
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
