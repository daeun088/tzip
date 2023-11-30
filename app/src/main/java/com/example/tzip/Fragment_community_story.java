package com.example.tzip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_community_story extends Fragment implements ModalBottomSheet.OnDismissListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_community_story() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_schedule_plan.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_community_story newInstance(String param1, String param2) {
        Fragment_community_story fragment = new Fragment_community_story();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_community_story, container, false);
        Button button = v.findViewById(R.id.);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
                modalBottomSheet.setOnDismissListener(ThirdActivity.this);
                modalBottomSheet.show(getSupportFragmentManager(), null);
            }
        });


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onDismis(boolean isSwitchOn) {
    }
}