package com.example.tzip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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
    int totalCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_trip_record, container, false);
        recordRecyclerView = view.findViewById(R.id.friend_record_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveFriendIds();
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

        // friendIds에 해당하는 친구들의 레코드 가져오기
        for (String friendId : friendIds) {
            FirebaseFirestore.getInstance().collection("record").document(friendId).collection("records")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Record record = document.toObject(Record.class);
                            String documentId = document.getId();
                            if (record != null) {
                                record.setDocumentId(documentId);
                                recordList.add(record);
                            }
                        }

                        // friendIds의 모든 친구의 레코드를 가져왔을 때 RecyclerView 설정
                        if (recordList.size() == friendIds.size()) {
                            // 레코드 리스트를 timestamp를 기준으로 정렬
                            Collections.sort(recordList);
                            setRecyclerView(recordList);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 에러 처리
                        Log.e("FetchRecords", "Error fetching records", e);
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
        // Record를 사용하여 상세 페이지 열기
        // TODO: 상세 페이지 열기 구현
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

        Glide.with(holder.itemView.getContext())
                .load(record.getContentImage())
                .skipMemoryCache(true)
                .into(holder.binding.feedPicture);

        holder.binding.feedPicture.setImageURI(record.getContentImage());
        holder.binding.recordTitle.setText(record.getTitle());
        holder.binding.place.setText(record.getPlace());
        holder.binding.date.setText(record.getDate());
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
