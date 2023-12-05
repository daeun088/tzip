package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FragmentMyTripRecordBinding;

public class MyTripRecord extends Fragment {
    private FragmentMyTripRecordBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MyTripRecord() {
        // Required empty public constructor
    }

    public static MyTripRecord newInstance(String param1, String param2) {
        MyTripRecord fragment = new MyTripRecord();
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
        binding = FragmentMyTripRecordBinding.inflate(inflater, container, false);

        //retrieverRecords();

        binding.recordAddBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            RecordAdd recordadd = new RecordAdd();
            transaction.replace(R.id.containers, recordadd);
            transaction.commit();
        });
        return binding.getRoot();
    }
}