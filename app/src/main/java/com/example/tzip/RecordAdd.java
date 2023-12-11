package com.example.tzip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RecordAdd extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore recordDB = FirebaseFirestore.getInstance();
    TextView dataPickerText;
    FragmentRecordAddBinding binding;
    Calendar calendar;

    private void callAddRecordWritingMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForRecordWriting(); // 액티비티의 메서드 호출
            activity.post_id = R.id.Record_add;
        }
    }

    public RecordAdd() {
        // Required empty public constructor
    }

    public static RecordAdd newInstance(String param1, String param2) {
        RecordAdd fragment = new RecordAdd();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 기존의 바인딩을 초기화합니다.
        binding = FragmentRecordAddBinding.inflate(inflater, container, false);

        dataPickerText = binding.tripDateText;
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        binding.recordAddBtn.setOnClickListener( v -> {
            String place = binding.tripPlace.getText().toString();
            String date = binding.tripDateText.getText().toString();
            String friend = binding.tripFriendList.getText().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (TextUtils.isEmpty(place)) {
                Toast.makeText(getContext(), "여행 장소를 설정해주세요", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(date)) {
                Toast.makeText(getContext(), "여행 날짜를 설정해주세요", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(friend)) {
                Toast.makeText(getContext(), "여행 참여자를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {

            CollectionReference recordsCollection = recordDB
                        .collection("record")
                        .document(uid)
                        .collection("records");

                Map<String, Object> recordMap = new HashMap<>();
                recordMap.put(FirebaseId.title, "null");
                recordMap.put(FirebaseId.place, place);
                recordMap.put(FirebaseId.date, date);
                recordMap.put(FirebaseId.friend, friend);
                recordMap.put(FirebaseId.contentImage, "null");
                recordMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());

                // 서브컬렉션 'records'에 새로운 문서 추가
                recordsCollection.add(recordMap)
                        .addOnSuccessListener(documentReference -> {
                            // 성공적으로 추가되었을 때의 작업
                            updateUI();
                        })
                        .addOnFailureListener(e -> {
                            // 실패했을 때의 작업
                            Log.e("Firestore", "Error adding record document", e);
                        });
            }

        });


        binding.tripDateText.setOnClickListener(v -> {
            if (isAdded()) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("여행 날짜를 선택해주세요");

                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "여행 날짜 설정");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");

                        Date date1 = new Date(selection.first);
                        Date date2 = new Date(selection.second);

                        String formattedDate1 = outputFormat.format(date1);
                        String formattedDate2 = outputFormat.format(date2);

                        dataPickerText.setText(formattedDate1 + "~" + formattedDate2);
                    }
                });
            }
        });

        return binding.getRoot();
    }

    private void updateUI() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        RecordWriting recordWriting = new RecordWriting();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseId.date, binding.tripDateText.getText().toString());
        bundle.putString(FirebaseId.place, binding.tripPlace.getText().toString());
        recordWriting.setArguments(bundle);

        callAddRecordWritingMethod();
        transaction.replace(R.id.containers, recordWriting);
        transaction.commit();

    }

}