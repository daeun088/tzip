package com.example.tzip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tzip.databinding.ActivityCommunityStoryInnerKakaoBinding;
import com.example.tzip.databinding.ActivityCommunityStoryInnerPeopleBinding;
import com.example.tzip.databinding.FragmentCommunityStoryBinding;
import com.example.tzip.databinding.FregmentCommunityAddBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment_community_story extends Fragment {

    FragmentCommunityStoryBinding binding;
    ActivityCommunityStoryInnerKakaoBinding bindingKakao;
    ActivityCommunityStoryInnerPeopleBinding bindingPeople;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomSheetDialog dialogPeople;
    private BottomSheetDialog dialogKakao;


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
        bindingKakao = ActivityCommunityStoryInnerKakaoBinding.inflate(inflater, container, false);
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
            bindingKakao.communityStoryInnerKakaolink.setText(kakaoLink);
            binding.communityStoryMoreexp.setText(moreExp);
        }

        binding.communityStoryImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        String uid = getUidOfCurrentUser();
        dialogPeople = new BottomSheetDialog(getContext());

        // 신청하기 누르면 인원 수 바텀 시트 나오게
        binding.communityStoryApply.setOnClickListener(v -> {
            View contentView = Fragment_community_story.this.getLayoutInflater().inflate(R.layout.activity_community_story_inner_people, null);
            dialogPeople.setContentView(contentView);
            attachListenerToContentView(contentView);
            dialogPeople.show();
        });
        // uid 주인에게 신청하기 버튼 안보이게

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
            if(storyPeople +  prePeo > allPeo) {
                Toast.makeText(getContext(), "최대 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                storyPeople++;
                bindingPeople.communityStoryInnerPeople.setText(String.valueOf(storyPeople));
            }
        });

        dialogKakao = new BottomSheetDialog(getContext());
        bindingPeople.communityStoryInnerApply.setOnClickListener(v -> {
            dialogPeople.dismiss();
            View kakaoView = Fragment_community_story.this.getLayoutInflater().inflate(R.layout.activity_community_story_inner_kakao, null);
            dialogKakao.setContentView(kakaoView);
            attachListenerToKakaoView(kakaoView);
            dialogKakao.show();
            addHyperlink(bindingKakao.communityStoryInnerKakaolink, "naver", "https://www.naver.com");
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

    private void showToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }
}