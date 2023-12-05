package com.example.tzip;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentRecordWritingBinding;
import com.example.tzip.databinding.FregmentRecordWriteInnerBinding;
import com.example.tzip.databinding.ItemWritingOfPlaceBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RecordWriting extends Fragment {
    FragmentRecordWritingBinding binding;
    private FirebaseFirestore recordBlockDB = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;
    final String[] lastestDocumentName = new String[1];
    private List<RecordItem> recordItems = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    private BottomSheetDialog dialog; // 바텀시트용 dialog 객체 <민>

    public RecordWriting() {
        // Required empty public constructor
    }
    public static RecordWriting newInstance(String param1, String param2) {
        RecordWriting fragment = new RecordWriting();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordWritingBinding.inflate(inflater, container, false);

        //add에서 입력된 데이터 불러오기
        Bundle bundle = getArguments();
        if (bundle != null) {
            String date = bundle.getString(FirebaseId.date, "");
            String place = bundle.getString(FirebaseId.place, "");
            binding.tripDate.setText("여행일시 - " + date);
            binding.tripPlace.setText("여행장소 - " + place);
        }

        //main image 띄우기
        binding.recordMainImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        binding.galleryBtn.setOnClickListener(v -> {
            //갤러리에서 사진 받아오면 현재 입력 중인 여행 블럭에 사진 추가한 후
            //밑에 다시 입력할 수 있는 뷰 띄우기
        });



        RecordItemAdapter adapter = new RecordItemAdapter(recordItems);
        binding.dayItem.setAdapter(adapter);
        binding.dayItem.setLayoutManager(new LinearLayoutManager(getContext()));

        // 바텀시트 띄우기 여기서부터 <민>
        dialog = new BottomSheetDialog(requireContext()); // requireContext 써도 되려나

        binding.addScheduleBtn.setOnClickListener(v -> {
            View contentView = RecordWriting.this.getLayoutInflater().inflate(R.layout.fregment_record_write_inner, null);
            dialog.setContentView(contentView);
            attachListenerToContentView(contentView);
            dialog.show();
        });
        // 여기까지 <민>

        return binding.getRoot();
    }

    private void retrievePlace() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // uid와 lastestDocumentName[0]가 null이 아닌지 확인
        if (uid != null && lastestDocumentName[0] != null) {
            db.collection("recordBlock").document(uid).collection(lastestDocumentName[0])
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            // 에러가 발생한 경우 처리
                            Log.e("Firestore", "Error getting data: ", e);
                            return;
                        }

                        // queryDocumentSnapshots가 null이 아니고 비어있지 않은 경우
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            // 데이터를 저장할 리스트 생성
                            List<RecordItem> updatedRecordItems = new ArrayList<>();

                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String date = document.getString(FirebaseId.date);
                                String detailPlace = document.getId();
                                String time = document.getString(FirebaseId.time);

                                RecordItem recordItem = new RecordItem();
                                recordItem.setDate(date);
                                recordItem.setTime(time);
                                recordItem.setBlockTitle(detailPlace);

                                // recordItems 리스트에 데이터 추가
                                updatedRecordItems.add(recordItem);
                            }

                            // 데이터를 갱신하고 RecyclerView 어댑터에 변경을 알림
                            recordItems.clear();
                            recordItems.addAll(updatedRecordItems);
                            if (binding.dayItem.getAdapter() != null) {
                                binding.dayItem.getAdapter().notifyDataSetChanged();
                            }
                        }
                    });
        } else {
            // uid나 lastestDocumentName[0]이 null이라면 에러 로그 출력
            Log.e("Firestore", "UID or document name is null");
        }
    }



    public class RecordItemAdapter extends RecyclerView.Adapter<RecordItemAdapter.ViewHolder> {
        private List<RecordItem> recordItems;

        public RecordItemAdapter(List<RecordItem> recordItems) {
            this.recordItems = recordItems;
            // 날짜 및 시간에 따라 정렬
            Collections.sort(recordItems, (item1, item2) -> {
                int dateComparison = item1.getDate().compareTo(item2.getDate());
                if (dateComparison == 0) {
                    // Date가 같으면 Time으로 정렬
                    return item1.getTime().compareTo(item2.getTime());
                }
                return dateComparison;
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemWritingOfPlaceBinding binding = ItemWritingOfPlaceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        // ... 기존 코드 유지
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecordItem recordItem = recordItems.get(position);

            holder.binding.date.setText(recordItem.getDate());
            holder.binding.blockTitle.setText(recordItem.getBlockTitle());
            holder.binding.blockTime.setText(recordItem.getTime());
            // 나머지 속성도 설정

            // 이미지 로딩 및 클릭 이벤트 처리
            // ...

            // 여기서는 RecordItem의 date가 같으면 time으로 정렬되어 있으므로
            // 첫 번째 아이템의 경우에만 날짜를 표시하도록 설정
            if (position == 0 || !recordItem.getDate().equals(recordItems.get(position - 1).getDate())) {
                holder.binding.date.setVisibility(View.VISIBLE);
            } else {
                holder.binding.date.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return recordItems.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemWritingOfPlaceBinding binding;

            public ViewHolder(ItemWritingOfPlaceBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }



    private void attachListenerToContentView(View contentView) {
        FregmentRecordWriteInnerBinding binding = FregmentRecordWriteInnerBinding.bind(contentView);
        //binding.scheduleBlockList.setLayoutManager(); 일정 블럭 리사이클러뷰

        binding.date.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dateDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String formattedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
                            // Set the formatted time to your TextView
                            binding.date.setText(formattedDate);
                        }
                    }, year, month, day
            );
            dateDialog.show(); // 다이얼로그를 표시하는 부분을 추가
        });


        binding.timeSetting.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                            // Set the formatted time to your TextView
                            binding.timeText.setText(formattedTime);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.addBtn.setOnClickListener(v -> {
            String date = binding.date.getText().toString();
            String detailPlace = binding.detailPlace.getText().toString();
            String time = binding.timeText.getText().toString();
            retrievePlace();


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference recordCollection = db.collection("record").document(uid).collection("records");

            //현재 records의 document Id 가져오기
            recordCollection
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // 문서가 존재하는 경우
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            lastestDocumentName[0] = document.getId();
                        }
                        //recordBlock → UID → ***(위랑 동일) → recordBlockTitle(==place) : date, time, content, image
                        // 생성 시 처음엔 title, date, time만 존재
                        CollectionReference recordBlockCollection = recordBlockDB
                                .collection("recordBlock")
                                .document(uid)
                                .collection(lastestDocumentName[0]);

                        Map<String, Object> recordBlockMap = new HashMap<>();
                        recordBlockMap.put(FirebaseId.date, date);
                        recordBlockMap.put(FirebaseId.time, time);

                        recordBlockCollection.document(detailPlace).set(recordBlockMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "gr", Toast.LENGTH_SHORT).show();
                                    //TODO : 바텀시트 닫기
                                })
                                .addOnFailureListener(e -> {
                                    // 추가 실패 시 처리
                                });
                    });
        });
    }
    // 이너페이지 안에 있는 애들 리스너 달아주기 <민>

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 선택된 이미지를 ImageView에 설정
            Uri imageUri = data.getData();
            binding.recordPicture.setImageURI(imageUri);

            // ImageView의 Drawable이 BitmapDrawable인지 확인하고 null이 아닌 경우에만 업로드
            Drawable drawable = binding.recordPicture.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                // 선택된 이미지를 Firebase Storage에 업로드
                uploadImageToFirebaseStorage(bitmap);
            } else {
                // 다른 Drawable 타입이거나 null인 경우에 대한 처리
                Log.e("daeun", "ImageView의 Drawable이 유효하지 않습니다.");
            }
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        // Get a default Storage bucket
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Points to the root reference
        StorageReference storageRef = storage.getReference();

        // Create a reference for a new image
        StorageReference mountainImagesRef = storageRef.child(getPath("jpg"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //0-100
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("daeun", "이미지뷰의 이미지 업로드 실패", exception);
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
            Log.d("daeun", "이미지뷰의 이미지 업로드 성공");
        });
    }


    public String getTitle(){
        return binding.recordTitle.getText().toString();
    }

    private String getPath(String extension) {
        String uid = getUidOfCurrentUser();

        String dir = (uid != null) ? uid : "public";

        String fileName = (uid != null) ? (uid + "_" + System.currentTimeMillis() + "." + extension)
                : ("anonymous" + "_" + System.currentTimeMillis() + "." + extension);

        return dir + "/" + fileName;
    }

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null ? true : false;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

}