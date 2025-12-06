package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.jhanpier.filmbox.R;

public class AvatarSelectorActivity extends AppCompatActivity {

    private int[] avatarList = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selector);

        GridView gridView = findViewById(R.id.gridAvatars);
        AvatarSelectorAdapter adapter = new AvatarSelectorAdapter(this, avatarList);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedAvatar = avatarList[position];
            Intent result = new Intent();
            result.putExtra("avatarResId", selectedAvatar);
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
