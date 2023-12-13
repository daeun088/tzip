package com.example.tzip;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.ActivityCommunityStoryInnerKakaoBinding;
import com.example.tzip.databinding.ActivityCommunityStoryInnerPeopleBinding;
import com.example.tzip.databinding.FragmentCommunityStoryBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Fragment_community_Read extends Fragment {
    private static List<RecordItem> communityItems = new ArrayList<>();
    String kakaoLink;
    static String uid;
    FragmentCommunityStoryBinding binding;
    private BottomSheetDialog dialogPeople;
    private BottomSheetDialog dialogKakao;

    ActivityCommunityStoryInnerKakaoBinding bindingKakao;
    ActivityCommunityStoryInnerPeopleBinding bindingPeople;

    public Fragment_community_Read() {
        // Required empty public constructor
    }

    public static RecordRead newInstance(String recordTitle) {
        return new RecordRead();
    }

    private static final String TAG = "Fragment_community_Read";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityStoryBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String place = bundle.getString(FirebaseId.place, "");
            String date = bundle.getString(FirebaseId.date, "");
            String time = bundle.getString(FirebaseId.time, "");
            String title = bundle.getString(FirebaseId.title, "");
            String prePeople = bundle.getString(FirebaseId.peopleCurrent, "");
            String allPeople = bundle.getString(FirebaseId.peopleAll, "");
            String moreExp = bundle.getString(FirebaseId.moreExplain, "");
            Log.d(TAG, "onCreateView: " + moreExp);
            String img = bundle.getString(FirebaseId.imageUrl, "");
            Log.d(TAG, "onCreateView: " + img);
            kakaoLink = bundle.getString(FirebaseId.kakaoLink, "");

            binding.communityStoryPlace.setText(place);
            binding.communityStoryDate.setText(date);
            binding.communityStoryTime.setText(time);
            binding.communityStoryTitle.setText(title);
            binding.communityStoryPrepeople.setText(prePeople);
            binding.communityStoryAllpeople.setText(allPeople);
            binding.communityStoryMoreexp.setText(moreExp);

            if (img != null && !img.isEmpty()) {
                Glide.with(binding.communityStoryImage.getContext())
                        .load(img)
                        .into(binding.communityStoryImage);
            } else {
                // 이미지 URL이 없을 때 디폴트 이미지 설정
                Glide.with(binding.communityStoryImage.getContext())
                        .load(R.drawable.schedule_example_pic) // 여기서 R.drawable.default_image는 디폴트 이미지의 리소스 ID입니다.
                        .into(binding.communityStoryImage);
            }

        }

        binding.communityStoryImageBtn.setVisibility(View.INVISIBLE);

        dialogPeople = new BottomSheetDialog(getContext());
        binding.communityStoryApply.setOnClickListener(v -> {
            View contentView = Fragment_community_Read.this.getLayoutInflater().inflate(R.layout.activity_community_story_inner_people, null);
            dialogPeople.setContentView(contentView);
            attachListenerToContentView(contentView);
            dialogPeople.show();
        });
        return binding.getRoot();
    }
    private int storyPeople;
    private int prePeo;
    private int allPeo;

    private void attachListenerToContentView(View contentView) {

        bindingPeople = ActivityCommunityStoryInnerPeopleBinding.bind(contentView);
        bindingPeople.communityStoryInnerPeople.setText("1");
        storyPeople = 1;
        prePeo = Integer.parseInt(binding.communityStoryPrepeople.getText().toString());
        allPeo = Integer.parseInt(binding.communityStoryAllpeople.getText().toString());

        bindingPeople.communityStoryInnerMinus.setOnClickListener(v -> {
            if(storyPeople == 1) {
                Toast.makeText(getContext(), "최소 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                storyPeople--;
                bindingPeople.communityStoryInnerPeople.setText(String.valueOf(storyPeople));
            }
        });
        bindingPeople.communityStoryInnerPlus.setOnClickListener(v -> {
            if(storyPeople +  prePeo >= allPeo) {
                Toast.makeText(getContext(), "최대 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                storyPeople++;
                bindingPeople.communityStoryInnerPeople.setText(String.valueOf(storyPeople));
            }
        });



        dialogKakao = new BottomSheetDialog(getContext());

        bindingPeople.communityStoryInnerApply.setOnClickListener(v -> {
            dialogPeople.dismiss();
            View kakaoView = Fragment_community_Read.this.getLayoutInflater().inflate(R.layout.activity_community_story_inner_kakao, null);
            dialogKakao.setContentView(kakaoView);
            attachListenerToKakaoView(kakaoView);
            dialogKakao.show();
            addHyperlink(bindingKakao.communityStoryInnerKakaolink, "kakaoLink", kakaoLink);
        });
    }

    private void attachListenerToKakaoView(View contentView) {
        bindingKakao = ActivityCommunityStoryInnerKakaoBinding.bind(contentView);
    }

    private void addHyperlink(TextView textView, String linkText, final String url) {
        // 텍스트에 하이퍼링크 추가
        String uid = getUidOfCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Linkify.addLinks(textView, Linkify.WEB_URLS);

        // 링크를 클릭할 때 처리를 추가
        textView.setOnClickListener(v -> {
            // 여기에서는 간단히 웹 브라우저를 열어 URL을 엽니다.
            // 더 복잡한 동작을 원한다면 여기에 적절한 로직을 추가하세요.
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });
    }

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

}
