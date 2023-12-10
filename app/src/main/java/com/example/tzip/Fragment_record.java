package com.example.tzip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentRecordBinding;
import com.example.tzip.databinding.FragmentScheduleBinding;

import org.jetbrains.annotations.Async;

import com.example.tzip.nevigation_bar_test_code;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            activity.post_id = R.id.review;
        }
    }
    private void callMyTripRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForMyTripRecord(); // 액티비티의 메서드 호출
            activity.post_id = R.id.review;
        }
    }

    private void callFriendTripRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForFriendTripRecord(); // 액티비티의 메서드 호출
            activity.post_id = R.id.review;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveRecords();
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

        binding.myTripText.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callMyTripRecordMethod();
            MyTripRecord mytriprecord = new MyTripRecord();
            transaction.replace(R.id.containers, mytriprecord);
            transaction.commit();
        });

        binding.recordAddBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callAddRecordMethod();
            RecordAdd recordadd = new RecordAdd();
            transaction.replace(R.id.containers, recordadd);
            transaction.commit();
        });

        binding.firstRecordBlock.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            RecordRead recordRead = new RecordRead();
            transaction.replace(R.id.containers, recordRead);
            transaction.commit();
        });

        return binding.getRoot();
    }

    private void retrieveRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // "record" 컬렉션에서 특정 UID 문서의 "records" 컬렉션을 가져오기
        db.collection("record").document(currentUserId).collection("records").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Record> recordList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // 여기서 document는 "records" 컬렉션 안의 각 문서를 나타냅니다.
                        // 가져온 문서를 Record 객체로 변환하여 사용할 수 있습니다.
                        Record record = document.toObject(Record.class);
                        if (record != null) {
                            recordList.add(record);
                        }
                    }
                            // 내림차순으로 정렬
                            Collections.sort(recordList);
                            List<Record> recentRecords = recordList.subList(0, Math.min(recordList.size(), 2));

                    if (!recentRecords.isEmpty()) {
                        binding.myRecords.setVisibility(View.VISIBLE);
                        binding.noRecords.setVisibility(View.GONE);
                        Glide.with(binding.tripBlockPicture)
                                .load(recentRecords.get(0).getContentImage())
                                .skipMemoryCache(true)// 이미지 URL 또는 URI
                                .into(binding.tripBlockPicture);
                        binding.tripBlockPicture.setImageURI(recentRecords.get(0).getContentImage());
                        // 여기서 최근 레코드를 사용하여 UI 업데이트
                        binding.tripBlockTitle.setText(recentRecords.get(0).getTitle());
                        binding.tripBlockDate.setText(recentRecords.get(0).getDate());
                        binding.tripBlockPlace.setText(recentRecords.get(0).getPlace());

                        if (recentRecords.size() >= 2) {
                            Glide.with(binding.tripBlockPicture2)
                                    .load(recentRecords.get(1).getContentImage()) // 이미지 URL 또는 URI
                                    .skipMemoryCache(true)
                                    .into(binding.tripBlockPicture2);
                            binding.tripBlockPicture2.setImageURI(recentRecords.get(1).getContentImage());
                            // 여기서 최근 레코드를 사용하여 UI 업데이트
                            binding.tripBlockTitle2.setText(recentRecords.get(1).getTitle());
                            binding.tripBlockDate2.setText(recentRecords.get(1).getDate());
                            binding.tripBlockPlace2.setText(recentRecords.get(1).getPlace());

                        } else {
                            binding.secondTripBlock.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // 에러 처리
                    Log.e("RetrieveRecords", "Error fetching records", e);
                });
    }
}