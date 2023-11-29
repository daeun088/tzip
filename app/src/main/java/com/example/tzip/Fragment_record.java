package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FragmentRecordBinding;

public class Fragment_record extends Fragment {
    private FragmentRecordBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_record() {
        // Required empty public constructor
    }

    public static Fragment_record newInstance(String param1, String param2) {
        Fragment_record fragment = new Fragment_record();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        binding.friendDetailBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            FriendTripRecord friendtriprecord = new FriendTripRecord();
            transaction.replace(R.id.containers, friendtriprecord);
            transaction.commit();
        });

        binding.myRecordDetailBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            MyTripRecord mytriprecord = new MyTripRecord();
            transaction.replace(R.id.containers, mytriprecord);
            transaction.commit();
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}