package com.example.tzip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.ActivityCommunityStoryInnerKakaoBinding;
import com.example.tzip.databinding.FragmentCommunityStoryBinding;
import com.example.tzip.databinding.FregmentCommunityAddBinding;
import com.google.firebase.auth.FirebaseAuth;

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

        binding.communityStoryImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        String uid = getUidOfCurrentUser();

        // uid 주인에게 신청하기 버튼 안보이게


        return binding.getRoot();
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // 선택한 이미지에 대한 처리...
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                if (selectedImageUri != null) {
                                    // 이미지 뷰에 선택한 이미지 설정
                                    binding.communityStoryImage.setImageURI(selectedImageUri);
                                }
                            }
                        }
                    });

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }
}