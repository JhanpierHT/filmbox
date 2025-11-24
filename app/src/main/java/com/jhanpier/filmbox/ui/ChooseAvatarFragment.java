package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.jhanpier.filmbox.R;

public class ChooseAvatarFragment extends Fragment {

    public ChooseAvatarFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_avatar, container, false);
        Button nextButton = view.findViewById(R.id.nextAvatarButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ChooseAvatarFragment.this)
                        .navigate(R.id.fragmentChoseAvatar);
            }
        });
        return view;
    }
}
