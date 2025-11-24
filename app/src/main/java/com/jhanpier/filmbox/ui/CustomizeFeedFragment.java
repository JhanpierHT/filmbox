package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.ChipGroup;
import com.jhanpier.filmbox.R;

public class CustomizeFeedFragment extends Fragment {

    public CustomizeFeedFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customize_feed, container, false);

        final ChipGroup genreChipGroup = view.findViewById(R.id.genreChipGroup);
        final ChipGroup movieChipGroup = view.findViewById(R.id.movieChipGroup);
        Button nextButton = view.findViewById(R.id.nextFeedButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenres = genreChipGroup.getCheckedChipIds().size();
                int selectedMovies = movieChipGroup.getCheckedChipIds().size();

                if (selectedGenres < 3 || selectedMovies < 3) {
                    Toast.makeText(requireContext(), "Select at least 3 genres and 3 movies", Toast.LENGTH_SHORT).show();
                } else {
                    NavHostFragment.findNavController(CustomizeFeedFragment.this)
                            .navigate(R.id.fragmentCustomizeFeed);
                }
            }
        });

        return view;
    }
}
