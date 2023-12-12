package com.example.tzip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentMypageBinding;
import com.example.tzip.databinding.ItemMypageFriendBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Fragment_mypage extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    String uid = currentUser.getUid();

    FirebaseFirestore userDB = FirebaseFirestore.getInstance();
    DocumentReference userDocRef = userDB.collection("user").document(uid);
    //Firestore에서 문서 참조

    FragmentMypageBinding binding;

    private String mParam1;
    private String mParam2;

    public Fragment_mypage() {
        // Required empty public constructor
    }

    private void callEmergecyMessageSettingMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForEmergencyMessage(); // 액티비티의 메서드 호출
            activity.post_id = R.id.mypage;
        }
    }

    private void callFriendListMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForFriendList(); // 액티비티의 메서드 호출
            activity.post_id = R.id.mypage;
        }
    }

    public static Fragment_mypage newInstance(String param1, String param2) {
        Fragment_mypage fragment = new Fragment_mypage();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Glide.get(requireContext()).clearMemory();
        binding = FragmentMypageBinding.inflate(inflater, container, false);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        String profile = document.getString("profileImage");
                        binding.userNickname.setText(nickname);
                        if (profile != null && !profile.isEmpty()) {
                            Glide.with(binding.profilePic)
                                    .load(profile)
                                    .skipMemoryCache(true)
                                    .into(binding.profilePic);
                        } else {
                            // 기본 이미지를 로드
                            Glide.with(binding.profilePic)
                                    .load(R.drawable.profilepic)  // 기본 이미지 리소스 ID
                                    .skipMemoryCache(true)
                                    .into(binding.profilePic);
                        }

                    }
                }
            }
        }); // user collection에서 uid로 접근 -> nickname 가져옴

        retrieveFriendIds();

        binding.moveFriendList.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callFriendListMethod();
            FriendList friendList = new FriendList();
            transaction.replace(R.id.containers, friendList);
            transaction.commit();
        });

        binding.settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });


        binding.logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), Login.class);
            getActivity().finish();
            startActivity(intent);
            //로그아웃 되었습니다 띄우고 login activity로 이동
        });

        binding.emergencySetting.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callEmergecyMessageSettingMethod();
            Fragment_emergency fragmentEmergency = new Fragment_emergency();
            transaction.replace(R.id.containers, fragmentEmergency);
            transaction.commit();
        });
        binding.sendBtn.setOnClickListener( v -> {
            Toast.makeText(getContext(), "긴급메세지가 발송되었습니다.", Toast.LENGTH_SHORT).show();
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }



    private void retrieveFriendIds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 friendIds 가져오기
        db.collection("friends").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendIds = (List<String>) documentSnapshot.get("friendIds");
                        if (friendIds != null) {
                            // friendIds를 사용하여 사용자 정보 조회
                            retrieveUserInformation(friendIds);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    //에러처리
                });
    }

    private void retrieveUserInformation(List<String> friendIds) {
        List<String> friendNames = new ArrayList<>();
        List<String> friendProfileImages = new ArrayList<>(); // 친구 프로필 이미지 URL 리스트 추가

        // friendIds에 해당하는 사용자 정보를 조회하고 friendNames 및 friendProfileImages에 추가
        int friendIdCount = friendIds.size();
        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("user").document(friendId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String friendName = documentSnapshot.getString("nickname");
                            String friendProfileImage = documentSnapshot.getString("profileImage"); // 프로필 이미지 URL 가져오기

                            friendNames.add(friendName);
                            friendProfileImages.add(friendProfileImage);

                            // 모든 사용자 정보를 조회한 경우에만 setRecyclerView 호출
                            if (friendNames.size() == friendIdCount) {
                                setRecyclerView(friendNames, friendProfileImages);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 오류 처리
                        // 모든 사용자 정보를 조회한 경우에만 setRecyclerView 호출
                        if (friendNames.size() == friendIdCount) {
                            setRecyclerView(friendNames, friendProfileImages);
                        }
                    });
        }
    }


    private void setRecyclerView(List<String> friendNames, List<String> friendProfileImages) {
        if (friendNames.isEmpty()) {
            // 친구가 없을 때
            binding.friendList.setVisibility(View.GONE);
            binding.noFriends.setVisibility(View.VISIBLE);
        } else {
            // 친구가 있을 때
            binding.friendList.setVisibility(View.VISIBLE);
            binding.noFriends.setVisibility(View.GONE);

            binding.friendList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
            binding.friendList.setAdapter(new Fragment_mypage.MyAdapter(friendNames, friendProfileImages));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // 이제 이 이미지를 Firebase Storage에 업로드하고, 다운로드 URL을 Firestore에 저장하는 작업을 수행합니다.
            uploadImageToStorage(imageUri);
            binding.profilePic.setImageURI(imageUri);
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        // Firebase Storage에 이미지 업로드 및 URL 가져오기
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + uid);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지 업로드 성공
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // 다운로드 URL을 Firestore에 저장
                        String imageUrl = uri.toString();
                        userDocRef.update("profileImage", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    // Firestore에 저장 성공
                                    Toast.makeText(getContext(), "프로필 이미지가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Firestore에 저장 실패
                                    Toast.makeText(getContext(), "프로필 이미지 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // 이미지 업로드 실패
                    Toast.makeText(getContext(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }


    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemMypageFriendBinding binding;

        private MyViewHolder(ItemMypageFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<Fragment_mypage.MyViewHolder> {
        private List<String> friendNames;
        private List<String> friendProfileImages;

        private MyAdapter(List<String> friendNames, List<String> friendProfileImages) {
            this.friendNames = friendNames;
            this.friendProfileImages = friendProfileImages;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMypageFriendBinding binding = ItemMypageFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String friendName = friendNames.get(position);
            String friendProfileImage = friendProfileImages.get(position);

            // Glide를 사용하여 이미지 로드
            if (friendProfileImage != null && !friendProfileImage.isEmpty()) {
                Glide.with(holder.binding.friendProfile)
                        .load(friendProfileImage)
                        .skipMemoryCache(true)
                        .into(holder.binding.friendProfile);
            } else {
                // 기본 이미지를 로드
                Glide.with(holder.binding.friendProfile)
                        .load(R.drawable.profilepic)  // 기본 이미지 리소스 ID
                        .skipMemoryCache(true)
                        .into(holder.binding.friendProfile);
            }

            holder.binding.friendName.setText(friendName);
        }

        @Override
        public int getItemCount() {
            return friendNames.size();
        }
    }
}