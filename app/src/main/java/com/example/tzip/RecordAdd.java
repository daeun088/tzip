package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RecordAdd extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView dataPickerText;
    FragmentRecordAddBinding binding;
    Calendar calendar;

    private String mParam1;
    private String mParam2;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 기존의 바인딩을 초기화합니다.
        binding = FragmentRecordAddBinding.inflate(inflater, container, false);

        dataPickerText = binding.tripDateText;
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Long today = MaterialDatePicker.todayInUtcMilliseconds();

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

}