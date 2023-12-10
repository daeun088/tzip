package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecordRead extends Fragment {
    private static final String ARG_RECORD_TITLE = "record_title";

    public RecordRead() {
        // Required empty public constructor
    }

    public static RecordRead newInstance(String recordTitle) {
        RecordRead fragment = new RecordRead();
        Bundle args = new Bundle();
        args.putString(ARG_RECORD_TITLE, recordTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_read, container, false);

        // 전달받은 title 표시
        TextView titleTextView = view.findViewById(R.id.text);
        if (getArguments() != null) {
            String recordTitle = getArguments().getString(ARG_RECORD_TITLE);
            titleTextView.setText(recordTitle);
        }

        return view;
    }
}
