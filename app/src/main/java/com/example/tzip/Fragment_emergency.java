package com.example.tzip;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentEmergencyBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment_emergency extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    FragmentEmergencyBinding binding;

    public Fragment_emergency() {
        // Required empty public constructor
    }

    public static Fragment_emergency newInstance(String param1, String param2) {
        Fragment_emergency fragment = new Fragment_emergency();
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
        // Inflate the layout for this fragment
        binding = FragmentEmergencyBinding.inflate(inflater, container, false);

        binding.emergencySettingBtn.setOnClickListener( v -> {
            Toast.makeText(getContext(), "긴급메세지 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment_mypage fragmentMypage = new Fragment_mypage();
            transaction.replace(R.id.containers, fragmentMypage);
            transaction.commit();
        });
        return binding.getRoot();
    }
}