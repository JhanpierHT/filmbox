package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jhanpier.filmbox.R;

public class ProfileSelectionFragment extends Fragment {

    private String param1;
    private String param2;

    public ProfileSelectionFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            param1 = getArguments().getString("param1");
            param2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_selection, container, false);
    }

    public static ProfileSelectionFragment newInstance(String p1, String p2) {
        ProfileSelectionFragment f = new ProfileSelectionFragment();
        Bundle b = new Bundle();
        b.putString("param1", p1);
        b.putString("param2", p2);
        f.setArguments(b);
        return f;
    }
}
