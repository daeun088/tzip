package com.example.tzip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentFriendRequestBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Fragment_friend_request extends Fragment {

    @NonNull FragmentFriendRequestBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private String mParam1;
    private String mParam2;

    public Fragment_friend_request() {
        // Required empty public constructor
    }

    public static Fragment_friend_request newInstance(String param1, String param2) {
        Fragment_friend_request fragment = new Fragment_friend_request();
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
        binding = FragmentFriendRequestBinding.inflate(inflater, container, false);

        binding.myId.setText(auth.getCurrentUser().getUid());

        binding.copyBtn.setOnClickListener( v -> {
            createClipData(binding.myId.getText().toString());
            Toast.makeText(getContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
        });//본인 ID 복사 기능

        binding.addBtn.setOnClickListener(v -> {
            String friendId = binding.friendId.getText().toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String myId = auth.getCurrentUser().getUid();

            if (friendId.isEmpty()) {
                Toast.makeText(getContext(), "친구 아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            // friendId와 현재 사용자의 UID 비교
            if (friendId.equals(myId)) {
                // friendId와 현재 사용자의 UID가 같은 경우
                Toast.makeText(getContext(), "자기 자신을 친구로 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return; // 추가 작업을 수행하지 않고 종료
            }

            // user collection에서 friendId의 document를 찾기
            DocumentReference userRef = db.collection("user").document(friendId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // user collection에서 해당 friendId의 document가 존재하는 경우
                        // friend collection에 현재 사용자의 UID를 document 이름으로 하는 document 생성
                        String currentUserId = auth.getCurrentUser().getUid();
                        DocumentReference friendRef = db.collection("friends").document(currentUserId);

                        // friendIds 필드가 이미 있는 경우에는 FieldValue.arrayUnion을 사용하여 값을 추가
                        friendRef.set(new HashMap<String, Object>() {{
                                    put("friendIds", FieldValue.arrayUnion(friendId));
                                    // 여기에 친구의 토큰을 저장
                                    put("friendTokens", FieldValue.arrayUnion(document.getString("fcmToken")));
                                }}, SetOptions.merge())
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        // 업데이트가 성공한 경우
                                        Toast.makeText(getContext(), "친구 추가 완료", Toast.LENGTH_SHORT).show();
                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        FriendList friendList = new FriendList();
                                        transaction.replace(R.id.containers, friendList);
                                        transaction.commit();
                                    } else {
                                        // 업데이트가 실패한 경우
                                        Toast.makeText(getContext(), "친구 추가 실패 : " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // user collection에서 해당 friendId의 document가 존재하지 않는 경우
                        Toast.makeText(getContext(), "유저가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return binding.getRoot();
    }

    public void createClipData(String message) {
        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", message);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }
}