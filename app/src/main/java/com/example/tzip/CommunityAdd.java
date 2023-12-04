package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FregmentCommunityAddBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CommunityAdd extends Fragment {

    private FirestoreRecyclerAdapter adapter;

    FregmentCommunityAddBinding binding;

    private FirebaseFirestore communityDB = FirebaseFirestore.getInstance();

    Calendar calendar;

    public static CommunityAdd newInstance() {
        CommunityAdd fragment = new CommunityAdd();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FregmentCommunityAddBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}