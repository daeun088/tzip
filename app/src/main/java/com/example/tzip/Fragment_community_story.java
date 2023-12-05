package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.ActivityCommunityStoryInnerKakaoBinding;
import com.example.tzip.databinding.FragmentCommunityStoryBinding;
import com.example.tzip.databinding.FregmentCommunityAddBinding;

public class Fragment_community_story extends Fragment {

    FragmentCommunityStoryBinding binding;
    ActivityCommunityStoryInnerKakaoBinding binding2;


    public Fragment_community_story() {
        // Required empty public constructor
    }
    public static Fragment_community_story newInstance(String param1, String param2) {
        Fragment_community_story fragment = new Fragment_community_story();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityStoryBinding.inflate(inflater, container, false);
        binding2 = ActivityCommunityStoryInnerKakaoBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String place = bundle.getString(FirebaseId.place, "");
            String date = bundle.getString(FirebaseId.date, "");
            String time = bundle.getString(FirebaseId.time, "");
            String title = bundle.getString(FirebaseId.title, "");
            String prePeople = bundle.getString(FirebaseId.peopleCurrent, "");
            String allPeople = bundle.getString(FirebaseId.peopleAll, "");
            String kakaoLink = bundle.getString(FirebaseId.kakaoLink, "");
            String moreExp = bundle.getString(FirebaseId.moreExplain, "");

            binding.communityStoryPlace.setText(place);
            binding.communityStoryDate.setText(date);
            binding.communityStoryTime.setText(time);
            binding.communityStoryTitle.setText(title);
            binding.communityStoryPrepeople.setText(prePeople);
            binding.communityStoryAllpeople.setText(allPeople);
            binding2.communityStoryInnerKakaolink.setText(kakaoLink);
            binding.communityStoryMoreexp.setText(moreExp);
        }


        return binding.getRoot();
    }
}