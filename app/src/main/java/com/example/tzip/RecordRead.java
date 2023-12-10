package com.example.tzip;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tzip.databinding.FragmentRecordReadBinding;

public class RecordRead extends Fragment {
    private static final String ARG_RECORD_TITLE = "record_title";

    public RecordRead() {
        // Required empty public constructor
    }

    public static RecordRead newInstance(String recordTitle) {
        return new RecordRead();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentRecordReadBinding binding = FragmentRecordReadBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_read, container, false);

        // 전달받은 title 표시
        TextView titleTextView = view.findViewById(R.id.record_title);
        TextView placeText = view.findViewById(R.id.trip_place);
        TextView dateText = view.findViewById(R.id.trip_date);
        ImageView mainImage = view.findViewById(R.id.record_picture);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "");
            String place = args.getString("place", "");
            String date = args.getString("date", "");
            Uri image = Uri.parse(args.getString("image",""));

            titleTextView.setText(title);
            placeText.setText("여행일시 - "+place);
            dateText.setText("여행날짜 - "+date);
            mainImage.setImageURI(image);

        }

        return view;
    }
}
