package com.example.tzip;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentMypageBinding;
import com.example.tzip.databinding.ItemFriendListBinding;
import com.example.tzip.databinding.ItemMypageFriendBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;


public class Fragment_mypage extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    String uid = currentUser.getUid();

    String fcmToken;

    String token;

    FirebaseFirestore userDB = FirebaseFirestore.getInstance();
    DocumentReference userDocRef = userDB.collection("user").document(uid);
    //Firestore에서 문서 참조

    Fragment_emergency fragmentEmergency;
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
        binding = FragmentMypageBinding.inflate(inflater, container, false);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        binding.userNickname.setText(nickname);

                    }
                }
            }
        }); // user collection에서 uid로 접근 -> nickname 가져옴

        retrieveFriendIds();

        binding.moveFriendList.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callFriendListMethod();
            FriendList friendList = new FriendList();
            transaction.replace(R.id.containers, friendList);
            transaction.commit();
        });

        binding.logout.setOnClickListener( v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), Login.class);
            getActivity().finish();
            startActivity(intent);
            //로그아웃 되었습니다 띄우고 login activity로 이동
        });

        binding.emergencySetting.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callEmergecyMessageSettingMethod();
            Fragment_emergency fragmentEmergency = new Fragment_emergency();
            transaction.replace(R.id.containers, fragmentEmergency);
            transaction.commit();
            fragmentEmergency.loadFromFirebase();
        });

        binding.sendBtn.setOnClickListener( v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 선언과 초기화
            List<String> TokenIds = new ArrayList<>();

            // Firestore에서 userToken 가져오기
            db.collection("userToken").document(currentUserId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userToken = documentSnapshot.getString(FirebaseId.token);

                            if (userToken != null) {
                                    sendNotification(userToken, " ", "");

                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 에러처리
                    });


            retrieveFriendTokens();
            retrieveToken();
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void retrieveToken() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        fcmToken = preferences.getString("FCM_TOKEN", null);

        if (fcmToken != null) {
            // 토큰을 가져왔으므로 사용 가능
            Log.d("YourFragment", "FCM Token: " + fcmToken);
        } else {
            // 저장된 토큰이 없는 경우
            Log.d("YourFragment", "FCM Token not found");
        }
    }
    private void retrieveFriendIds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 friendIds 가져오기
        db.collection("userToken").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> TokenIds = (List<String>) documentSnapshot.get("userToken");
                        if (TokenIds != null) {
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    //에러처리
                });
    }

    private void retrieveUserInformation(List<String> friendIds) {
        List<String> friendNames = new ArrayList<>();

        // friendIds에 해당하는 사용자 정보를 조회하고 friendNames에 추가
        int friendIdCount = friendIds.size();
        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("user").document(friendId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String friendName = documentSnapshot.getString("nickname");
                            friendNames.add(friendName);

                            // 모든 사용자 정보를 조회한 경우에만 setRecyclerView 호출
                            if (friendNames.size() == friendIdCount) {
                                setRecyclerView(friendNames);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 오류 처리
                        // 모든 사용자 정보를 조회한 경우에만 setRecyclerView 호출
                        if (friendNames.size() == friendIdCount) {
                            setRecyclerView(friendNames);
                        }
                    });
        }
    }
            private void setRecyclerView(List<String> friendNames) {
        if (friendNames.isEmpty()) {
            // 친구가 없을 때
            binding.friendList.setVisibility(View.GONE);
            binding.noFriends.setVisibility(View.VISIBLE);
        } else {
            // 친구가 있을 때
            binding.friendList.setVisibility(View.VISIBLE);
            binding.noFriends.setVisibility(View.GONE);

            binding.friendList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
            binding.friendList.setAdapter(new Fragment_mypage.MyAdapter(friendNames));
        }
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

        private MyAdapter(List<String> friendNames) {
            this.friendNames = friendNames;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMypageFriendBinding binding = ItemMypageFriendBinding.inflate(LayoutInflater.from(parent.getContext()) ,parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String friendName = friendNames.get(position);
            holder.binding.friendName.setText(friendName);
        }

        @Override
        public int getItemCount() {
            return friendNames.size();
        }
    }
    private void sendNotification(String to, String title, String message) {
        try {
            // FCM 메시지 생성
            JSONObject messageJson = new JSONObject();
            messageJson.put("to", to);

            // 알림 관련 정보 추가
            JSONObject notificationJson = new JSONObject();
            notificationJson.put("title", title);
            notificationJson.put("body", message);

            messageJson.put("notification", notificationJson);

            // FCM 서버에 메시지 전송
            String FCM_SERVER_URL = "https://fcm.googleapis.com/fcm/send";
            String FCM_SERVER_KEY = "AAAA8a0-qh0:APA91bEVrJJkUDQwYJNExMN4zignRfps_FNBlJFSgOv2McIdHGlpSIfqUxyIb5Uc0TjTc9G3H70wo_zpcsOI8nHfZMx6WfkPo7_E306kXjvudr4Fpz7HpH--_hOsWe0i-ffA1V56uoqZ";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    FCM_SERVER_URL,
                    messageJson,
                    response -> {
                        // 성공적으로 메시지를 전송한 경우의 처리a
                    },
                    error -> {
                        // 메시지 전송 실패 시의 처리
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    // 요청 헤더에 FCM 서버 키 추가
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "key=" + FCM_SERVER_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // 요청을 큐에 추가
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void retrieveFriendTokens() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 friendTokens 가져오기
        db.collection("friends").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendTokens = (List<String>) documentSnapshot.get("friendTokens");

                        for (String token : friendTokens) {
                            sendNotification(token," ", "qwe");
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    //에러처리
                });
    }

}