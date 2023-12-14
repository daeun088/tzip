package com.example.tzip;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentCommunityListBinding;
import com.example.tzip.databinding.FragmentFriendListBinding;
import com.example.tzip.databinding.ItemCommunityInnerBinding;
import com.example.tzip.databinding.ItemFriendListBinding;
import com.example.tzip.databinding.ItemHomeListBinding;
import com.example.tzip.databinding.ItemMypageCommunityListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityList extends Fragment {
    private static final String TAG = "CommunityList";

    FragmentCommunityListBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore friendDB = FirebaseFirestore.getInstance();

    public CommunityList() {
        // Required empty public constructor
    }
    public static CommunityList newInstance(String param1, String param2) {
        CommunityList fragment = new CommunityList();
        return fragment;
    }

    private void callCommunityListMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForCommunityList(); // 액티비티의 메서드 호출
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityListBinding.inflate(inflater, container, false);

        retrieveStoryIds(); // 이게 뭐노 //

        binding.addFriendBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callCommunityListMethod();
            Fragment_community fragmentCommunity = new Fragment_community();
            transaction.replace(R.id.containers, fragmentCommunity);
            transaction.commit();
        });
        // 얘도 필요 없나 //


        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void retrieveStoryIds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<CommunityListDataSet> list = new ArrayList<>();

        final String[] tempT = new String[1];
        final String[] tempL = new String[1];
        final String[] tempD = new String[1];
        final String[] tempI = new String[1];

        // Firestore에서 friendIds 가져오기
        db.collection("community")
                .document(currentUserId)
                .collection("storys")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                tempT[0] = document.getString(FirebaseId.title);
                                tempL[0] = document.getString(FirebaseId.place);
                                tempD[0] = document.getString(FirebaseId.date);
                                tempI[0] = document.getString(FirebaseId.imageUrl);
                                Log.d(TAG, "onComplete: " + tempT[0]+ " " + tempD[0] + " " + tempL[0] + " " + tempI[0]);

                                list.add(new CommunityListDataSet(tempT[0], tempL[0], tempD[0], tempI[0]));

                                binding.communityList.setLayoutManager(new LinearLayoutManager(requireContext()));
                                binding.communityList.setAdapter(new MyAdapter(list));

                                Log.d(TAG, "onComplete: " + list.size());

                                setRecyclerView(list);
                            }
                        }
                    }
                });
    }

    private void setRecyclerView(List<CommunityListDataSet> list) {
        if (list.isEmpty()) {
            // 친구가 없을 때
            binding.communityStoryNum.setText("게시글 0");
            binding.communityList.setVisibility(View.GONE);
            binding.noFriends.setVisibility(View.VISIBLE);
        } else {
            // 친구가 있을 때
            binding.communityStoryNum.setText("게시글 " + list.size());
            binding.communityList.setVisibility(View.VISIBLE);
            binding.noFriends.setVisibility(View.GONE);
        }
    }


    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemMypageCommunityListBinding binding;

        private MyViewHolder(ItemMypageCommunityListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<CommunityListDataSet> list;

        private MyAdapter(List<CommunityListDataSet> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMypageCommunityListBinding binding = ItemMypageCommunityListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CommunityListDataSet communityListDataSet = list.get(position);

            String title = communityListDataSet.getTitle();
            String location = communityListDataSet.getLocation();
            String date = communityListDataSet.getDate();
            String img = communityListDataSet.getImg();

            holder.binding.mypageCommunityTitle.setText(title);
            holder.binding.mypageCommunityLocation.setText(location);
            holder.binding.mypageCommunityDate.setText(date);

            // Glide를 사용하여 프로필 이미지 로드
            if (img != null && !img.isEmpty()) {
                Glide.with(holder.binding.mypageCommunityImage)
                        .load(img)
                        .into(holder.binding.mypageCommunityImage);
            } else {
                // 기본 이미지를 로드
                Glide.with(holder.binding.mypageCommunityImage)
                        .load(R.drawable.profilepic)  // 기본 이미지 리소스 ID
                        .into(holder.binding.mypageCommunityImage);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class CommunityListDataSet {
        String title;
        String location;
        String date;
        String img;
        private CommunityListDataSet(String src_title, String src_location, String src_date, String src_img) {
            title = src_title;
            location = src_location;
            date = src_date;
            img = src_img;
        }

        public String getLocation() {
            return location;
        }

        public String getImg() {
            return img;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }
    }
}