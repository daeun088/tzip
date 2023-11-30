package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RecordAdd extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore recordDB = FirebaseFirestore.getInstance();
    TextView dataPickerText;
    FragmentRecordAddBinding binding;
    Calendar calendar;


    public RecordAdd() {
        // Required empty public constructor
    }

    public static RecordAdd newInstance(String param1, String param2) {
        RecordAdd fragment = new RecordAdd();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 기존의 바인딩을 초기화합니다.
        binding = FragmentRecordAddBinding.inflate(inflater, container, false);

        dataPickerText = binding.tripDateText;
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        binding.recordAddBtn.setOnClickListener( v -> {
            CollectionReference record = recordDB.collection("record");
            String place = binding.tripPlace.getText().toString();
            String date = binding.tripDateText.getText().toString();
            String friend = binding.tripFriendList.getText().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put(FirebaseId.documentId,uid);
            recordMap.put(FirebaseId.recordTitle, "null");
            recordMap.put(FirebaseId.place, place);
            recordMap.put(FirebaseId.date, date);
            recordMap.put(FirebaseId.friend, friend);
            recordMap.put(FirebaseId.contentImage, "null");
            recordMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
            recordMap.put("recordBlock", "null");
            //record.document("record").set(recordMap); document 이름 지정 코드
            record.document().set(recordMap);

            updateUI();
        });


        binding.tripDateBtn.setOnClickListener(v -> {
            if (isAdded()) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Data Picker");

                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);

                        String dateString1 = simpleDateFormat.format(date1);
                        String dateString2 = simpleDateFormat.format(date2);

                        dataPickerText.setText(dateString1 + "~" + dateString2);
                    }
                });
            }
        });

        return binding.getRoot();
    }

    private void updateUI() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        RecordWriting recordWriting = new RecordWriting();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseId.date, binding.tripDateText.getText().toString());
        bundle.putString(FirebaseId.place, binding.tripPlace.getText().toString());
        recordWriting.setArguments(bundle);

        transaction.replace(R.id.containers, recordWriting);
        transaction.commit();
    }

}