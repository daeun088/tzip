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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Fragment_community_story extends Fragment {

    FragmentCommunityStoryBinding binding;
    ActivityCommunityStoryInnerKakaoBinding bindingKakao;
    ActivityCommunityStoryInnerPeopleBinding bindingPeople;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomSheetDialog dialogPeople;
    private BottomSheetDialog dialogKakao;
    String imageUrl;
    String currentDocName;


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
            currentDocName = bundle.getString(FirebaseId.currentDocId, "");

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

        // uid 주인에게 신청하기 버튼 안보이게
        binding.communityStoryApply.setVisibility(View.INVISIBLE);

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

                                    uploadImageToStorage(selectedImageUri);
                                }
                            }
                        }
                    });


    private void uploadImageToStorage(Uri imageUri) {
        String uid = getUidOfCurrentUser();

        String imageName = "community_image_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("community_images").child(uid).child(imageName);
// 이미지 업로드
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지 업로드 성공
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // 업로드된 이미지의 다운로드 URL 획득 성공
                        imageUrl = uri.toString();
                        updateImageUriToFireStore(imageUrl);
                    });
                });

    }

    private void updateImageUriToFireStore(String url) {
        String uid = getUidOfCurrentUser();

        DocumentReference dRef = db.collection("community")
                .document(uid)
                .collection("storys")
                .document(currentDocName);

        dRef.update("imageUrl", url);
    }

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