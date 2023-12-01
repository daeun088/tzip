package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FragmentRecordBinding;
import com.example.tzip.databinding.FragmentScheduleBinding;

import org.jetbrains.annotations.Async;

import com.example.tzip.nevigation_bar_test_code;

public class Fragment_record extends Fragment {
    private FragmentRecordBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_record() {
        // Required empty public constructor
    }
    private void callScheduleMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForSchedule(); // 액티비티의 메서드 호출
        }
    }
    private void callAddRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForAddRecord(); // 액티비티의 메서드 호출
        }
    }
    private void callMyTripRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForMyTripRecord(); // 액티비티의 메서드 호출
        }
    }

    private void callFriendTripRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForFriendTripRecord(); // 액티비티의 메서드 호출
        }
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
            callFriendTripRecordMethod();
            FriendTripRecord friendtriprecord = new FriendTripRecord();
            transaction.replace(R.id.containers, friendtriprecord);
            transaction.commit();
        });

        binding.myRecordDetailBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callMyTripRecordMethod();
            MyTripRecord mytriprecord = new MyTripRecord();
            transaction.replace(R.id.containers, mytriprecord);
            transaction.commit();
        });

        binding.scheduleAddBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callScheduleMethod();
            Fragment_schedule schedule = new Fragment_schedule();
            transaction.replace(R.id.containers, schedule);
            transaction.commit();
        });

        binding.recordAddBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callAddRecordMethod();
            RecordAdd recordadd = new RecordAdd();
            transaction.replace(R.id.containers, recordadd);
            transaction.commit();
        });
        // Inflate the layout for this fragment
        //네비게이션바로 인텐트 넘기는 거 전달해줘야될 수도
        return binding.getRoot();
    }
}