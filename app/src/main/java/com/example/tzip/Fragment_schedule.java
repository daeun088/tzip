package com.example.tzip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.example.tzip.databinding.FragmentScheduleBinding;
import com.example.tzip.databinding.FregmentRecordWriteInnerBinding;
import com.example.tzip.databinding.ScheduleInnerBinding;
import com.example.tzip.databinding.ScheduleMainRecyclerviewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_schedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_schedule extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int GALLERY_REQUEST_CODE = 123;

    Fragment_schedule_plan fragmentSchedulePlan;

    FragmentScheduleBinding binding;

    ScheduleMainRecyclerviewBinding binding2;
    BottomSheetDialog dialog;

    TextView tripPeriodText;

    Calendar calendar;

    Button schedule_add_btn;

    int itemCount;
    EditText scheduleTitleEditText;


    EditText schedulePlaceEditText;


    ScheduleInnerBinding scheduleInnerBinding;

    View contentView;

    String title;

    String date;

    String place;

    String imageUrl;

    int num = 0;



    private FirebaseFirestore scheduleDB = FirebaseFirestore.getInstance();


    private void callSchedulePlanMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForSchedulePlan(); // 액티비티의 메서드 호출
            activity.post_id = R.id.schedule;
        }
    }

    public Fragment_schedule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_schedule.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_schedule newInstance(String param1, String param2) {
        Fragment_schedule fragment = new Fragment_schedule();
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
        binding = FragmentScheduleBinding.inflate(inflater, container, false);


        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        loadScheduleDataFromFirestore();


        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            list.add("Item=" + i);
        }

        binding.scheduleBlockList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.scheduleBlockList.setAdapter(new Fragment_schedule.MyAdapter(list));
        binding.scheduleBlockList.addItemDecoration(new Fragment_schedule.MyItemDecoration());
        return binding.getRoot();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ScheduleMainRecyclerviewBinding binding;

        private MyViewHolder(ScheduleMainRecyclerviewBinding binding) {
            super(binding.getRoot());
            dialog = new BottomSheetDialog((getContext()));
            this.binding = binding;

            binding.scheduleBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentSchedulePlan = new Fragment_schedule_plan();
                    transaction.replace(R.id.containers, fragmentSchedulePlan).commit();
                    callSchedulePlanMethod();
                }
            });
            binding.scheduleAdd.setOnClickListener(v -> {
                View contentView = Fragment_schedule.this.getLayoutInflater().inflate(R.layout.schedule_inner, null);
                dialog.setContentView(contentView);
                attachListenerToContentView(contentView);
                dialog.show();
            });


        }
    }

    private void attachListenerToContentView(View contentView) {
        scheduleInnerBinding = ScheduleInnerBinding.bind(contentView);
        scheduleInnerBinding.tripDateBtn.setOnClickListener(v -> {
            if (isAdded()) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Data Picker");

                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);

                        String dateString1 = simpleDateFormat.format(date1);
                        String dateString2 = simpleDateFormat.format(date2);

                        scheduleInnerBinding.tripPeriod.setText(dateString1 + "~" + dateString2);
                    }
                });
            }
        });


        scheduleInnerBinding.scheduleAddBtn.setOnClickListener(v -> {
            dialog.dismiss();
            title = scheduleInnerBinding.scheduleTitle.getText().toString();
            date = scheduleInnerBinding.tripPeriod.getText().toString();
            place = scheduleInnerBinding.schedulePlace.getText().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 사용자의 uid로 기록 서브컬렉션에 접근
            CollectionReference schedulesCollection = scheduleDB
                    .collection("schedule")
                    .document(uid)
                    .collection("schedules");

            Map<String, Object> scheduleMap = new HashMap<>();
            scheduleMap.put(FirebaseId.title, title);
            scheduleMap.put(FirebaseId.place, place);
            scheduleMap.put(FirebaseId.date, date);
            scheduleMap.put(FirebaseId.contentImage, "null");
            scheduleMap.put(FirebaseId.imageUrl, imageUrl);

            scheduleMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
            String newItem = "Title: " + title + "\nDate: " + date + "\n";

            // 어댑터에 아이템 추가
            ((MyAdapter) binding.scheduleBlockList.getAdapter()).addItem(newItem);

            // 서브컬렉션 'records'에 새로운 문서 추가
            schedulesCollection.add(scheduleMap)
                    .addOnSuccessListener(documentReference -> {
                        // 성공적으로 추가되었을 때의 작업
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // 실패했을 때의 작업
                        Log.e("Firestore", "Error adding schedule document", e);
                        dialog.dismiss();
                    });
        });

        scheduleInnerBinding.scheduleBlockPic.setOnClickListener(v -> {
            // 갤러리 열기
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });





        num++;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                // 선택한 이미지의 URI를 가져옴
                Uri imageUri = data.getData();

                // 이미지를 ImageView에 설정
                scheduleInnerBinding.scheduleBlockPic.setImageURI(imageUri);

                // Firestore에 이미지 업로드 및 저장 등 추가 작업 수행
                uploadImageToFirestore(imageUri);
            }
        }
    }

    private void uploadImageToFirestore(Uri imageUri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String imageName = "schedule_image_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("schedule_images").child(uid).child(imageName);
// 이미지 업로드
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지 업로드 성공
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // 업로드된 이미지의 다운로드 URL 획득 성공
                        imageUrl = uri.toString();


                    });
                });

    }





    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> list;

        public void addAllItems(List<String> newItems) {
            list.addAll(0, newItems);
            notifyDataSetChanged();
        }

        public void addItem(String newItem) {
            list.add(0, newItem);
            notifyItemInserted(0);
        }

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment_schedule.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ScheduleMainRecyclerviewBinding binding = ScheduleMainRecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull Fragment_schedule.MyViewHolder holder, int position) {
            String text = list.get(position);
            String titleText = extractTitle(text);
            String dateText = extractDate(text);
            ScheduleMainRecyclerviewBinding scheduleBinding = (ScheduleMainRecyclerviewBinding) holder.binding;



            if(itemCount > 0) {
                if (position == itemCount + num) {
                    // 뷰 홀더에 각각 설정
                    scheduleBinding.scheduleBlockTitle.setText(titleText);
                    scheduleBinding.scheduleTripPeriod.setText(dateText);
                    scheduleBinding.scheduleBlock.setVisibility(View.GONE);
                    scheduleBinding.scheduleAdd.setVisibility(View.VISIBLE);
                } else {
                    // 뷰 홀더에 각각 설정
                    scheduleBinding.scheduleBlockTitle.setText(titleText);
                    scheduleBinding.scheduleTripPeriod.setText(dateText);
                    scheduleBinding.scheduleBlock.setVisibility(View.VISIBLE);
                    scheduleBinding.scheduleAdd.setVisibility(View.GONE);
                    loadScheduleImage(scheduleBinding.scheduleBlockPic, position);
                }
            } else {
                if (getItemCount() == 1) {
                    // 뷰 홀더에 각각 설정
                    scheduleBinding.scheduleBlockTitle.setText(titleText);
                    scheduleBinding.scheduleTripPeriod.setText(dateText);
                    scheduleBinding.scheduleBlock.setVisibility(View.GONE);
                    scheduleBinding.scheduleAdd.setVisibility(View.VISIBLE);
                } else {
                    // 뷰 홀더에 각각 설정
                    scheduleBinding.scheduleBlockTitle.setText(titleText);
                    scheduleBinding.scheduleTripPeriod.setText(dateText);
                    scheduleBinding.scheduleBlock.setVisibility(View.VISIBLE);
                    scheduleBinding.scheduleAdd.setVisibility(View.GONE);
                    loadScheduleImage(scheduleBinding.scheduleBlockPic, position);
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int index = parent.getChildAdapterPosition(view) + 1;

            if (index % 3 == 0)
                outRect.set(20, 20, 20, 60);
            else
                outRect.set(20, 20, 20, 20);
            ViewCompat.setElevation(view, 20.0f);
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        }
    }


    private String extractTitle(String text) {
        // "Title: " 다음에 오는 문자열을 추출
        int startIndex = text.indexOf("Title: ") + "Title: ".length();
        int endIndex = text.indexOf("\n", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex).trim();
        } else {
            // 적절한 로직을 사용하여 title을 추출할 수 없는 경우 처리
            return "Title Not Found";
        }
    }

    // date를 추출하는 메소드
    private String extractDate(String text) {
        // "Date: " 다음에 오는 문자열을 추출
        int startIndex = text.indexOf("Date: ") + "Date: ".length();
        int endIndex = text.indexOf("\n", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex).trim();
        } else {
            // 적절한 로직을 사용하여 date를 추출할 수 없는 경우 처리
            return "Date Not Found";
        }
    }

    private void loadScheduleDataFromFirestore() {
        num = 0;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = scheduleDB
                .collection("schedule")
                .document(uid)
                .collection("schedules");

        schedulesCollection.orderBy(FirebaseId.timestamp, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> itemList = new ArrayList<>();
                    itemCount = queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot loadedData : queryDocumentSnapshots) {

                        // 각 문서에서 필요한 데이터를 추출하여 itemList에 추가
                        String title = loadedData.getString(FirebaseId.title);
                        String place = loadedData.getString(FirebaseId.place);
                        String date = loadedData.getString(FirebaseId.date);
                        itemList.add("Title: " + title + "\nDate: " + date + "\n");
                    }

                    // RecyclerView 어댑터에 데이터를 설정
                    if (binding.scheduleBlockList.getAdapter() instanceof MyAdapter) {
                        ((MyAdapter) binding.scheduleBlockList.getAdapter()).addAllItems(itemList);
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터 불러오기 실패 시의 작업
                    Log.e("Firestore", "Error getting documents: ", e);
                });
    }

    private void loadScheduleImage(ImageView imageView, int position) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = scheduleDB
                .collection("schedule")
                .document(uid)
                .collection("schedules");

        schedulesCollection.orderBy(FirebaseId.timestamp, Query.Direction.DESCENDING)
                .limit(position + 1)  // 데이터 검색 로직에 맞게 조정
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty() && position < queryDocumentSnapshots.size()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(position);
                        imageUrl = documentSnapshot.getString(FirebaseId.imageUrl);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(imageUrl)
                                    .into(imageView);
                        } else {
                            // 이미지 URL이 없을 때 디폴트 이미지 설정
                            Glide.with(imageView.getContext())
                                    .load(R.drawable.schedule_example_pic) // 여기서 R.drawable.default_image는 디폴트 이미지의 리소스 ID입니다.
                                    .into(imageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // 이미지 불러오기 실패 시의 작업
                    Log.e("Firestore", "Error getting documents: ", e);
                });
    }
}

