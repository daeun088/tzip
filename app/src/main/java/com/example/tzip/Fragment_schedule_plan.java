package com.example.tzip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentScheduleBinding;
import com.example.tzip.databinding.FragmentSchedulePlanBinding;
import com.example.tzip.databinding.ScheduleInnerBinding;
import com.example.tzip.databinding.ScheduleMainRecyclerviewBinding;
import com.example.tzip.databinding.SchedulePlanInnerBinding;
import com.example.tzip.databinding.SchedulePlanRecyclerviewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
 * Use the {@link Fragment_schedule_plan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_schedule_plan extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int GALLERY_REQUEST_CODE = 123;

    FragmentSchedulePlanBinding binding;

    Fragment_schedule fragmentSchedule;
    Fragment_schedule_plan_write fragmentSchedulePlanWrite;

    BottomSheetDialog dialog;

    Calendar calendar;

    SchedulePlanInnerBinding schedulePlanInnerBinding;

    String title;

    String place;

    String time;

    String date;

    int num = 0;

    int itemCount;

    String imageUrl;

    private FirebaseFirestore schedulePlanDB = FirebaseFirestore.getInstance();

    public Fragment_schedule_plan() {
        // Required empty public constructor
    }

    private void callSchedulePlanWriteMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForSchedulePlanWrite(); // 액티비티의 메서드 호출
            activity.post_id = R.id.Schedule_plan;
        }
    }


    public static Fragment_schedule_plan newInstance(String param1, String param2) {
        Fragment_schedule_plan fragment = new Fragment_schedule_plan();
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
        binding = FragmentSchedulePlanBinding.inflate(inflater, container, false);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        loadScheduleDataFromFirestore();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            list.add("Item=" + i);
        }

        binding.schedulePlanBlockList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.schedulePlanBlockList.setAdapter(new Fragment_schedule_plan.MyAdapter(list));
        binding.schedulePlanBlockList.addItemDecoration(new Fragment_schedule_plan.MyItemDecoration());

        return binding.getRoot();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private SchedulePlanRecyclerviewBinding binding;

        private MyViewHolder(SchedulePlanRecyclerviewBinding binding) {
            super(binding.getRoot());
            dialog = new BottomSheetDialog((getContext()));
            this.binding = binding;
            binding.schedulePlanBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentSchedulePlanWrite = new Fragment_schedule_plan_write();
                    transaction.replace(R.id.containers, fragmentSchedulePlanWrite).commit();
                    callSchedulePlanWriteMethod();
                }
            });
            binding.schedulePlanAdd.setOnClickListener(v -> {
                View contentView = Fragment_schedule_plan.this.getLayoutInflater().inflate(R.layout.schedule_plan_inner, null);
                dialog.setContentView(contentView);
                attachListenerToContentView(contentView);
                dialog.show();
            });
        }
    }

    private void attachListenerToContentView(View contentView) {
        schedulePlanInnerBinding = SchedulePlanInnerBinding.bind(contentView);
        schedulePlanInnerBinding.schedulePlanDate.setOnClickListener(v -> {
            // DatePickerDialog를 생성
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                // 선택된 날짜를 처리
                String selectedDate = String.format(Locale.getDefault(), "%04d년 %02d월 %02d일", selectedYear, selectedMonth + 1, selectedDay);
                schedulePlanInnerBinding.schedulePlanDateSet.setText(selectedDate);
            }, year, month, day);

            // DatePickerDialog를 표시
            datePickerDialog.show();

                });



        schedulePlanInnerBinding.schedulePlanAddBtn.setOnClickListener(v -> {
            dialog.dismiss();
            title = schedulePlanInnerBinding.schedulePlanTitle.getText().toString();
            time = schedulePlanInnerBinding.schedulePlanTimeSet.getText().toString();
            place = schedulePlanInnerBinding.schedulePlanPlace.getText().toString();
            date = schedulePlanInnerBinding.schedulePlanDateSet.getText().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 사용자의 uid로 기록 서브컬렉션에 접근
            CollectionReference schedulesCollection = schedulePlanDB
                    .collection("schedulePlanBlock")
                    .document(uid)
                    .collection("scheduleBlocks");

            Map<String, Object> schedulePlanMap = new HashMap<>();
            schedulePlanMap.put(FirebaseId.title, title);
            schedulePlanMap.put(FirebaseId.place, place);
            schedulePlanMap.put(FirebaseId.time, time);
            schedulePlanMap.put(FirebaseId.date, date);
            schedulePlanMap.put(FirebaseId.contentImage, "null");
            schedulePlanMap.put(FirebaseId.imageUrl, imageUrl);

            // Date와 Time을 합친 dateTime 필드 추가
            String dateTime = date + " " + time;
            schedulePlanMap.put("dateTime", dateTime);

            schedulePlanMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
            String newItem = "Title: " + title + "\nPlace: " + place + "\nDate: " + date + "\nTime: " + time + "\n";

            // 어댑터에 아이템 추가
            ((Fragment_schedule_plan.MyAdapter) binding.schedulePlanBlockList.getAdapter()).addItem(newItem);

            // 서브컬렉션 'records'에 새로운 문서 추가
            schedulesCollection.add(schedulePlanMap)
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

        schedulePlanInnerBinding.schedulePlanTime.setOnClickListener(v -> {
            // TimePickerDialog를 생성
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // 선택된 시간을 처리
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    schedulePlanInnerBinding.schedulePlanTimeSet.setText(selectedTime);
                }
            }, /* 초기 시간 설정 */ 12, 0, false);

            // TimePickerDialog를 표시
            timePickerDialog.show();

        });

        schedulePlanInnerBinding.schedulePlanBlockPic.setOnClickListener(v -> {
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
                schedulePlanInnerBinding.schedulePlanBlockPic.setImageURI(imageUri);

                // Firestore에 이미지 업로드 및 저장 등 추가 작업 수행
                uploadImageToFirestore(imageUri);
            }
        }
    }

    private void uploadImageToFirestore(Uri imageUri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String imageName = "schedule_Plan_image_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("schedule_plan_images").child(uid).child(imageName);
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

    private class MyAdapter extends RecyclerView.Adapter<Fragment_schedule_plan.MyViewHolder> {
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
        public Fragment_schedule_plan.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SchedulePlanRecyclerviewBinding binding = SchedulePlanRecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull Fragment_schedule_plan.MyViewHolder holder, int position) {
            String text = list.get(position);
            String titleText = extractTitle(text);
            String placeText = extractPlace(text);
            String DateText = extractDate(text);
            String TimeText = extractTime(text);
            SchedulePlanRecyclerviewBinding schedulePlanBinding = (SchedulePlanRecyclerviewBinding) holder.binding;


            if(itemCount > 0) {
                if (position == itemCount + num) {
                    // 뷰 홀더에 각각 설정
                    schedulePlanBinding.schedulePlanTitle.setText(titleText);
                    schedulePlanBinding.schedulePlanBlockDate.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanBlockTime.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanPlace.setText(placeText);
                    schedulePlanBinding.schedulePlanBlock.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanAdd.setVisibility(View.VISIBLE);
                } else {
                    // 뷰 홀더에 각각 설정
                    schedulePlanBinding.schedulePlanTitle.setText(titleText);
                    schedulePlanBinding.schedulePlanBlockDate.setText(DateText);
                    schedulePlanBinding.schedulePlanBlockTime.setText(TimeText);
                    schedulePlanBinding.schedulePlanBlockDate.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanBlockTime.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanPlace.setText(placeText);
                    schedulePlanBinding.schedulePlanBlock.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanAdd.setVisibility(View.GONE);
                    loadScheduleImage(schedulePlanBinding.schedulePlanPic, position);
                }
            } else {
                if (getItemCount() == 1) {
                    // 뷰 홀더에 각각 설정
                    schedulePlanBinding.schedulePlanTitle.setText(titleText);
                    schedulePlanBinding.schedulePlanBlockDate.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanBlockTime.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanPlace.setText(placeText);
                    schedulePlanBinding.schedulePlanBlock.setVisibility(View.GONE);
                    schedulePlanBinding.schedulePlanAdd.setVisibility(View.VISIBLE);
                } else {
                    // 뷰 홀더에 각각 설정
                    schedulePlanBinding.schedulePlanTitle.setText(titleText);
                    schedulePlanBinding.schedulePlanBlockDate.setText(DateText);
                    schedulePlanBinding.schedulePlanBlockTime.setText(TimeText);
                    schedulePlanBinding.schedulePlanBlockDate.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanBlockTime.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanPlace.setText(placeText);
                    schedulePlanBinding.schedulePlanBlock.setVisibility(View.VISIBLE);
                    schedulePlanBinding.schedulePlanAdd.setVisibility(View.GONE);
                    loadScheduleImage(schedulePlanBinding.schedulePlanPic, position);
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

    private String extractDate(String text) {
        // "Title: " 다음에 오는 문자열을 추출
        int startIndex = text.indexOf("Date: ") + "Date: ".length();
        int endIndex = text.indexOf("\n", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex).trim();
        } else {
            // 적절한 로직을 사용하여 title을 추출할 수 없는 경우 처리
            return "Date Not Found";
        }
    }

    private String extractTime(String text) {
        // "Title: " 다음에 오는 문자열을 추출
        int startIndex = text.indexOf("Time: ") + "Time: ".length();
        int endIndex = text.indexOf("\n", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex).trim();
        } else {
            // 적절한 로직을 사용하여 title을 추출할 수 없는 경우 처리
            return "Time Not Found";
        }
    }

    // date를 추출하는 메소드
    private String extractPlace(String text) {
        // "Date: " 다음에 오는 문자열을 추출
        int startIndex = text.indexOf("Place: ") + "Place: ".length();
        int endIndex = text.indexOf("\n", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex).trim();
        } else {
            // 적절한 로직을 사용하여 date를 추출할 수 없는 경우 처리
            return "Place Not Found";
        }
    }

    private void loadScheduleDataFromFirestore() {
        num = 0;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = schedulePlanDB
                .collection("schedulePlanBlock")
                .document(uid)
                .collection("scheduleBlocks");

        schedulesCollection.orderBy("dateTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> itemList = new ArrayList<>();
                    itemCount = queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot loadedData : queryDocumentSnapshots) {

                        // 각 문서에서 필요한 데이터를 추출하여 itemList에 추가
                        String title = loadedData.getString(FirebaseId.title);
                        String place = loadedData.getString(FirebaseId.place);
                        String date = loadedData.getString(FirebaseId.date);
                        String time = loadedData.getString(FirebaseId.time);
                        itemList.add("Title: " + title + "\nPlace: " + place + "\nDate: " + date + "\nTime: " + time + "\n");
                    }

                    // RecyclerView 어댑터에 데이터를 설정
                    if (binding.schedulePlanBlockList.getAdapter() instanceof MyAdapter) {
                        ((MyAdapter) binding.schedulePlanBlockList.getAdapter()).addAllItems(itemList);
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터 불러오기 실패 시의 작업
                    Log.e("Firestore", "Error getting documents: ", e);
                });
    }

    private void loadScheduleImage(ImageView imageView, int position) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = schedulePlanDB
                .collection("schedulePlanBlock")
                .document(uid)
                .collection("scheduleBlocks");

        schedulesCollection.orderBy("dateTime", Query.Direction.ASCENDING)
                .limit(position + 1)  // 데이터 검색 로직에 맞게 조정
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty() && position < queryDocumentSnapshots.size()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(position);
                        String imageUrl = documentSnapshot.getString(FirebaseId.imageUrl);

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