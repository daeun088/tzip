package com.example.tzip;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentFriendTripRecordBinding;
import com.example.tzip.databinding.ItemFriendListBinding;
import com.example.tzip.databinding.ItemFriendRecordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendTripRecord extends Fragment {

    private RecyclerView recordRecyclerView;
    private FriendRecordAdapter recordAdapter;
    private FragmentFriendTripRecordBinding binding;
    int totalCount = 0;
    int queryCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendTripRecordBinding.inflate(inflater, container, false);

        recordRecyclerView = binding.friendRecordList;  // RecyclerView 초기화

        retrieveFriendIds();

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
                            retrieveRecords(friendIds);
                            String friendName = documentSnapshot.getString("nickname");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // 에러 처리
                    Log.e("FetchFriends", "Error fetching friends", e);
                });
    }

    // FriendTripRecord 클래스의 retrieveRecords 메서드 내부
    private void retrieveRecords(List<String> friendIds) {
        List<Record> recordList = new ArrayList<>();


        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("record").document(friendId).collection("records")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Record> friendRecordList = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Record record = document.toObject(Record.class);
                            String documentId = document.getId();
                            if (record != null) {
                                record.setDocumentId(documentId);
                                record.setFriendId(friendId);

                                // Firestore에서 friendName 가져오기
                                FirebaseFirestore.getInstance().collection("user").document(friendId).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            String friendName = documentSnapshot.getString("nickname");
                                            record.setFriendName(friendName);

                                            // 모든 데이터가 준비되었을 때 RecyclerView 설정
                                            queryCount++;
                                            if (queryCount == friendIds.size() && friendRecordList.size() >= 2) {
                                                Collections.sort(friendRecordList);
                                                setRecyclerView(friendRecordList);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // 에러 처리
                                            Log.e("FetchRecords", "Error fetching friend name", e);
                                            queryCount++; // 에러가 발생하더라도 queryCount를 증가시켜서 모든 쿼리가 완료되었다고 판단하도록 함
                                            if (queryCount == friendIds.size() && friendRecordList.size() >= 2) {
                                                Collections.sort(friendRecordList);
                                                setRecyclerView(friendRecordList);
                                            }
                                        });

                                friendRecordList.add(record);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 에러 처리
                        Log.e("FetchRecords", "Error fetching records", e);
                        queryCount++; // 에러가 발생하더라도 queryCount를 증가시켜서 모든 쿼리가 완료되었다고 판단하도록 함
                        if (queryCount == friendIds.size()) {
                            // 레코드 리스트를 timestamp를 기준으로 정렬
                            Collections.sort(recordList);
                            setRecyclerView(recordList);
                        }
                    });
        }
    }

        private int getTotalRecordCount(List<String> friendIds) {

        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("record").document(friendId).collection("records")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        totalCount += querySnapshot.size();
                    })
                    .addOnFailureListener(e -> {
                        // 에러 처리
                        Log.e("FetchRecordCount", "Error fetching record count", e);
                    });
        }

        return totalCount;
    }

    private void setRecyclerView(List<Record> recordList) {
        recordAdapter = new FriendRecordAdapter(recordList, record -> openDetailPage(record));
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordRecyclerView.setAdapter(recordAdapter);
    }

    private void openDetailPage(Record record) {
        String recordTitle = record.getTitle();
        String recordPlace = record.getPlace();
        String recordDate = record.getDate();
        Uri recordImage = record.getContentImage();
        String documentId = record.getDocumentId();
        String uid = record.getFriendId();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // 세부 정보를 표시할 프래그먼트 생성
        RecordRead detailFragment = new RecordRead();

        // 프래그먼트에 전달할 번들 생성
        Bundle bundle = new Bundle();
        bundle.putString("title", recordTitle);
        bundle.putString("place", recordPlace);
        bundle.putString("date", recordDate);
        bundle.putString("image", String.valueOf(recordImage));
        bundle.putString("documentId", documentId);
        bundle.putString("uid", uid);

        //bundle.putString("document", documentName);

        // 번들을 프래그먼트에 설정
        detailFragment.setArguments(bundle);

        // 생성한 프래그먼트를 교체
        transaction.replace(R.id.containers, detailFragment);

        // 트랜잭션 커밋
        transaction.commit();
    }
}


class FriendRecordAdapter extends RecyclerView.Adapter<FriendRecordAdapter.FriendHolder> {
    private List<Record> recordList;
    private FriendRecordAdapter.OnItemClickListener itemClickListener;

    public FriendRecordAdapter(List<Record> recordList, FriendRecordAdapter.OnItemClickListener listener) {
        this.recordList = recordList;
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Record record);
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendRecordBinding binding = ItemFriendRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        Record record = recordList.get(position);

        Glide.with(holder.binding.feedPicture.getContext())
                .load(record.getContentImage())
                .skipMemoryCache(true)
                .into(holder.binding.feedPicture);

        holder.binding.feedPicture.setImageURI(record.getContentImage());
        holder.binding.recordTitle.setText(record.getTitle());
        holder.binding.place.setText(record.getPlace());
        holder.binding.date.setText(record.getDate());
        holder.binding.userName.setText(record.getFriendName());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    @Override
    public void onViewRecycled(@NonNull FriendHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.binding.feedPicture);
    }

    class FriendHolder extends RecyclerView.ViewHolder {
        private ItemFriendRecordBinding binding;

        private FriendHolder(ItemFriendRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.recordBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(recordList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
