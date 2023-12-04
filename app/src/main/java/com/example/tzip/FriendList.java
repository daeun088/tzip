package com.example.tzip;

import android.content.ClipData;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FragmentFriendListBinding;
import com.example.tzip.databinding.FragmentFriendRequestBinding;
import com.example.tzip.databinding.ItemFriendListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends Fragment {

    FragmentFriendListBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore friendDB = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FriendList() {
        // Required empty public constructor
    }
    public static FriendList newInstance(String param1, String param2) {
        FriendList fragment = new FriendList();
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
//TODO: friend collection → my uid document → friend Id 갖고 옴
//TODO: friend Id를 갖고 user collection에 접근 → 각 friend Id document로 field 갖고오기 → name, profile pic 띄우기
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendListBinding.inflate(inflater, container, false);


        retrieveFriendIds();



        binding.addFriendBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment_friend_request fragmentFriendRequest = new Fragment_friend_request();
            transaction.replace(R.id.containers, fragmentFriendRequest);
            transaction.commit();
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
                    // 오류 처리
                });
    }

    private void retrieveUserInformation(List<String> friendIds) {
        List<String> friendNames = new ArrayList<>();

        // friendIds에 해당하는 사용자 정보를 조회하고 friendNames에 추가
        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("user").document(friendId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String friendName = documentSnapshot.getString("nickname");
                            friendNames.add(friendName);

                            // friendNames가 완성되면 RecyclerView에 설정
                            setRecyclerView(friendNames);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 오류 처리
                    });
        }
    }
    private void setRecyclerView(List<String> friendNames) {
        binding.friendNum.setText("친구 " + friendNames.size());
        binding.friendList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.friendList.setAdapter(new MyAdapter(friendNames));
        binding.friendList.addItemDecoration(new MyItemDecoration());
    }


    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendListBinding binding;

        private MyViewHolder(ItemFriendListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> friendNames;

        private MyAdapter(List<String> friendNames) {
            this.friendNames = friendNames;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFriendListBinding binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    private static class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int index = parent.getChildAdapterPosition(view) + 1;

            if (index % 3 == 0)
                outRect.set(20, 20, 20, 60);
            else
                outRect.set(20, 20, 20, 20);
        }
    }
}