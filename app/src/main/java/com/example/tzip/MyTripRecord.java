package com.example.tzip;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentMyTripRecordBinding;
import com.example.tzip.databinding.ItemRecordListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyTripRecord extends Fragment {
    private FragmentMyTripRecordBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private String mParam1;
    private String mParam2;

    public MyTripRecord() {
        // Required empty public constructor
    }

    public static MyTripRecord newInstance(String param1, String param2) {
        MyTripRecord fragment = new MyTripRecord();
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
        binding = FragmentMyTripRecordBinding.inflate(inflater, container, false);

        retrieveRecords();

        binding.recordAddBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            RecordAdd recordadd = new RecordAdd();
            transaction.replace(R.id.containers, recordadd);
            transaction.commit();
        });
        return binding.getRoot();
    }

    private void retrieveRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // "record" 컬렉션에서 특정 UID 문서의 "records" 컬렉션을 가져오기
        db.collection("record").document(currentUserId).collection("records").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Record> recordList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // 여기서 document는 "records" 컬렉션 안의 각 문서를 나타냅니다.
                        // 가져온 문서를 Record 객체로 변환하여 사용할 수 있습니다.
                        Record record = document.toObject(Record.class);
                        String documentId = document.getId();
                        if (record != null) {
                            record.setDocumentId(documentId);
                            recordList.add(record);
                        }
                        if (!recordList.isEmpty()) {
                            setRecyclerView(recordList);
                            // 내림차순으로 정렬
                            Collections.sort(recordList);
                        } else {
                            binding.recordSize.setText("0개");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // 에러 처리
                    Log.e("RetrieveRecords", "Error fetching records", e);
                });
    }

    private void setRecyclerView(List<Record> recordList) {
        if (recordList.isEmpty()) {
            binding.recordSize.setText("0개");
        } else {
            binding.recordSize.setText(recordList.size() + "개");
            binding.noRecord.setVisibility(View.GONE);

            binding.recordList.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recordList.setAdapter(new RecordAdapter(recordList, new RecordAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Record record) {
                    openDetailPage(record);
                }
            }));
        }
    }

    private void openDetailPage(Record record) {
            String recordTitle = record.getTitle();
            String recordPlace = record.getPlace();
            String recordDate = record.getDate();
            Uri recordImage = record.getContentImage();
            String documentId = record.getDocumentId();

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
            bundle.putString("uid",currentUserId);
            //bundle.putString("document", documentName);

            // 번들을 프래그먼트에 설정
            detailFragment.setArguments(bundle);

            // 생성한 프래그먼트를 교체
            transaction.replace(R.id.containers, detailFragment);

            // 트랜잭션 커밋
            transaction.commit();
    }
}

class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {
    private List<Record> recordList;
    private OnItemClickListener itemClickListener;

    public RecordAdapter(List<Record> recordList, OnItemClickListener listener) {
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
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecordListBinding binding = ItemRecordListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecordHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
        Record record = recordList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(record.getContentImage())
                .skipMemoryCache(true)
                .into(holder.binding.feedPicture);

        holder.binding.feedPicture.setImageURI(record.getContentImage());
        holder.binding.title.setText(record.getTitle());
        holder.binding.place.setText(record.getPlace());
        holder.binding.date.setText(record.getDate());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    @Override
    public void onViewRecycled(@NonNull RecordHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.binding.feedPicture);
    }

    class RecordHolder extends RecyclerView.ViewHolder {
        private ItemRecordListBinding binding;

        private RecordHolder(ItemRecordListBinding binding) {
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