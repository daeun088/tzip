package com.example.tzip;

import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecordRead extends Fragment {
    private static final String ARG_RECORD_TITLE = "record_title";

    private String document;
    View view;
    View ItemView;
    private static List<RecordItem> recordItems = new ArrayList<>();
    static String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public RecordRead() {
        // Required empty public constructor
    }

    public static RecordRead newInstance(String recordTitle) {
        return new RecordRead();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_record_read, container, false);

        // 전달받은 title 표시
        TextView titleTextView = view.findViewById(R.id.record_title);
        TextView placeText = view.findViewById(R.id.trip_place);
        TextView dateText = view.findViewById(R.id.trip_date);
        ImageView mainImage = view.findViewById(R.id.record_picture);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "");
            String place = args.getString("place", "");
            String date = args.getString("date", "");
            Uri image = Uri.parse(args.getString("image", ""));
            document = args.getString("documentId", "");

            titleTextView.setText(title);
            placeText.setText("여행일시 - " + place);
            dateText.setText("여행날짜 - " + date);
            mainImage.setImageURI(image);

            Glide.with(mainImage.getContext())
                    .load(image)
                    .skipMemoryCache(true)
                    .into(mainImage);
        }

        retrievePlace();

        RecyclerView recyclerView = view.findViewById(R.id.dayItem);
        RecordAdapter adapter = new RecordAdapter(recordItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void retrievePlace() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // uid와 document가 null이 아닌지 확인
        if (uid != null && document != null) {
            db.collection("recordBlock").document(uid).collection(document)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            // 에러가 발생한 경우 처리
                            Log.e("Firestore", "Error getting data: ", e);
                            return;
                        }

                        // queryDocumentSnapshots가 null이 아니고 비어있지 않은 경우
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            // 데이터를 저장할 리스트 생성
                            List<RecordItem> recordBlockList = new ArrayList<>();

                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String date = document.getString(FirebaseId.date);
                                String detailPlace = document.getId();
                                String time = document.getString(FirebaseId.time);
                                String text = document.getString(FirebaseId.text);
                                String image = document.getString(FirebaseId.contentImage);

                                RecordItem recordItem = new RecordItem();
                                recordItem.setDate(date);
                                recordItem.setTime(time);
                                recordItem.setBlockTitle(detailPlace);
                                recordItem.setText(text);
                                recordItem.setContentImage(image);
                                Log.d("daeun", date);

                                recordBlockList.add(recordItem);
                            }

                            recordBlockList.sort(new RecordItem.ItemSort());

                            recordItems.clear();
                            recordItems.addAll(recordBlockList);
                            if (((RecyclerView)view.findViewById(R.id.dayItem)).getAdapter() != null) {
                                ((RecordAdapter) ((RecyclerView)view.findViewById(R.id.dayItem)).getAdapter()).setRecordItems(recordItems);
                            }
                        }
                    });
        } else {
            // uid나 document가 null이라면 에러 로그 출력
            Log.e("Firestore", "UID or document name is null");
        }
    }

    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
        private List<RecordItem> recordItems;

        public RecordAdapter(List<RecordItem> recordItems) {
            this.recordItems = recordItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_read, parent, false);
            return new ViewHolder(ItemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecordItem recordItem = recordItems.get(position);

            Log.d("daeun", "Position: " + position + ", Date: " + recordItem.getDate());

            Glide.with(holder.blockImage.getContext())
                    .load(recordItem.getContentImage())
                    .skipMemoryCache(true)
                    .into(holder.blockImage);

            holder.date.setText(recordItem.getDate());
            holder.blockTitle.setText(recordItem.getBlockTitle());
            holder.blockTime.setText(recordItem.getTime());
            holder.content.setText(recordItem.getText());
            holder.blockImage.setImageURI(recordItem.getContentImage());

            // 여기서는 RecordItem의 date가 같으면 time으로 정렬되어 있으므로
            // 첫 번째 아이템의 경우에만 날짜를 표시하도록 설정
            if (position == 0 || recordItem.getDate() == null || !recordItem.getDate().equals(recordItems.get(position - 1).getDate())) {
                holder.date.setVisibility(View.VISIBLE);
            } else {
                holder.date.setVisibility(View.GONE);
            }
        }

        public void setRecordItems(List<RecordItem> recordItems) {
            this.recordItems = recordItems;
            Collections.sort(recordItems, (item1, item2) -> {
                Date date1 = item1.getDateTimeObject();
                Date date2 = item2.getDateTimeObject();

                if (date1 != null && date2 != null) {
                    return date1.compareTo(date2);
                } else {
                    return 0;
                }
            });

            notifyDataSetChanged(); // Notify the adapter about the data set change
        }

        @Override
        public int getItemCount() {
            return recordItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView blockImage;
            TextView date;
            TextView blockTitle;
            TextView blockTime;
            TextView content;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                blockImage = itemView.findViewById(R.id.block_image);
                date = itemView.findViewById(R.id.date);
                blockTitle = itemView.findViewById(R.id.block_title);
                blockTime = itemView.findViewById(R.id.block_time);
                content = itemView.findViewById(R.id.content);
            }
        }
    }
}
