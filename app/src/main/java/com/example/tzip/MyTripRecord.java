package com.example.tzip;

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

        binding.recordAddBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            RecordAdd recordadd = new RecordAdd();
            transaction.replace(R.id.containers, recordadd);
            transaction.commit();
        });
        return binding.getRoot();
    }

    private void retrieveRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // "record" 컬렉션에서 특정 UID 문서의 "records" 컬렉션을 가져오기
        db.collection("record").document(currentUserId).collection("records").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Record> recordList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // 여기서 document는 "records" 컬렉션 안의 각 문서를 나타냅니다.
                        // 가져온 문서를 Record 객체로 변환하여 사용할 수 있습니다.
                        Record record = document.toObject(Record.class);
                        if (record != null) {
                            recordList.add(record);
                        }
                        if (!recordList.isEmpty()) {
                            setRecyclerView(recordList);
                            // 내림차순으로 정렬
                            Collections.sort(recordList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                                // 에러 처리
                                Log.e("RetrieveRecords", "Error fetching records", e);
                            });
                        }

    private void setRecyclerView(List<Record> recordList) {
        if(recordList.isEmpty()) {
            binding.recordSize.setText("0개");
        } else {
            binding.recordSize.setText(recordList.size()+"개");
            binding.noRecord.setVisibility(View.GONE);

            binding.recordList.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recordList.setAdapter(new RecordAdapter(recordList));
        }
    }

    private static class RecordHolder extends RecyclerView.ViewHolder {
        private ItemRecordListBinding binding;
        private RecordHolder(ItemRecordListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        private List<Record> recordList;
        private RecordAdapter(List<Record> recordList) {this.recordList = recordList;}
        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRecordListBinding binding = ItemRecordListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecordHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
            Record record = recordList.get(position);

            holder.binding.feedPicture.setImageURI(record.getContentImage());
            holder.binding.title.setText(record.getTitle());
            holder.binding.place.setText(record.getPlace());
            holder.binding.date.setText(record.getDate());

        }

        @Override
        public int getItemCount() {
            return recordList.size();
        }
    }
}

