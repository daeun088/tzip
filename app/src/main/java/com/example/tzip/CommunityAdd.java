package com.example.tzip;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tzip.databinding.ActivityCommunityInnerpageBinding;
import com.example.tzip.databinding.FregmentCommunityAddBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;

public class CommunityAdd extends Fragment {
    FregmentCommunityAddBinding binding;
    ActivityCommunityInnerpageBinding binding2;

    private BottomSheetDialog dialog; // 바텀시트용 dialog 객체 <민>

    private FirebaseFirestore communityDB = FirebaseFirestore.getInstance();
    String currentDocName;

    private void callAddCommunityStoryMethod() {
        if(getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setTollbarForCommunityStory();
        }
    }


    public static CommunityAdd newInstance() {
        CommunityAdd fragment = new CommunityAdd();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FregmentCommunityAddBinding.inflate(inflater, container, false);

        binding.communityAddDateBtn.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dateDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String formattedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
                            binding.communityAddDate.setText(formattedDate);
                        }
                    }, year, month, day
            );
            dateDialog.show();
        });

        binding.communityAddTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                            // Set the formatted time to your TextView
                            binding.communityAddTime.setText(formattedTime);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        dialog = new BottomSheetDialog(getContext());

        binding.communityAddBtn.setOnClickListener(v -> {
            String date = binding.communityAddDate.getText().toString();
            String place = binding.communityAddPlace.getText().toString();
            String time = binding.communityAddTime.getText().toString();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String[] lastestDocumentName = new String[1];

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference communityCollection = db.collection("community").document(uid).collection("storys");

            Map<String, Object> communityAddMap = new HashMap<>();
            communityAddMap.put(FirebaseId.title, "null");
            communityAddMap.put(FirebaseId.date, date);
            communityAddMap.put(FirebaseId.place, place);
            communityAddMap.put(FirebaseId.peopleCurrent, "null");
            communityAddMap.put(FirebaseId.peopleAll, "null");
            communityAddMap.put(FirebaseId.kakaoLink, "null");
            communityAddMap.put(FirebaseId.moreExplain, "null");
            communityAddMap.put(FirebaseId.time, time);
            communityAddMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
            communityAddMap.put(FirebaseId.imageUrl, "null");

            communityCollection.add(communityAddMap)
                    .addOnSuccessListener(documentReference -> {
                        // 성공
                        // 바텀시트 띄우기 여기서부터 <민>
                        View contentView = CommunityAdd.this.getLayoutInflater().inflate(R.layout.activity_community_innerpage, null);
                        dialog.setContentView(contentView);
                        attachListenerToContentView(contentView);
                        dialog.show();
                        Toast.makeText(getContext(), "우와아아앙", Toast.LENGTH_SHORT);
                        // 여기까지 <민>
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding", e);
                    });


        });


        return binding.getRoot();
    }

    private int prePeople = 1;
    private int allPeople = 1;

    private void attachListenerToContentView(View contentView) {

        binding2 = ActivityCommunityInnerpageBinding.bind(contentView);

        binding2.communityInnerAllpeople.setText("1");
        binding2.communityInnerPresentpeople.setText("1");


        binding2.communityInnerMinus1.setOnClickListener(v -> {
            if (prePeople == 1) {
                Toast.makeText(getContext(), "최소 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                prePeople--;
                binding2.communityInnerPresentpeople.setText(String.valueOf(prePeople));
            }
        });
        binding2.communityInnerMinus2.setOnClickListener(v -> {
            if (allPeople == prePeople) {
                Toast.makeText(getContext(), "최소 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                allPeople--;
                binding2.communityInnerAllpeople.setText(String.valueOf(allPeople));
            }
        });
        binding2.communityInnerPlus1.setOnClickListener(v -> {
            if (prePeople == allPeople) {
                Toast.makeText(getContext(), "최대 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                prePeople++;
                binding2.communityInnerPresentpeople.setText(String.valueOf(prePeople));
            }
        });
        binding2.communityInnerPlus2.setOnClickListener(v -> {
            if (allPeople == 99) {
                Toast.makeText(getContext(), "최대 인원입니다.", Toast.LENGTH_SHORT).show();
            } else {
                allPeople++;
                binding2.communityInnerAllpeople.setText(String.valueOf(allPeople));
            }
        });

        binding2.communityInnerInvite.setOnClickListener(v -> {
            String title = binding2.communityInnerTitle.getText().toString();
            String prePeople = binding2.communityInnerPresentpeople.getText().toString();
            String allPeople = binding2.communityInnerAllpeople.getText().toString();
            String kakaoLink = binding2.communityInnerKakaoLink.getText().toString();
            String moreExp = binding2.communityInnerMoreExplain.getText().toString();
            final String[] nickname = new String[1];
            final String[] profileImage = new String[1];

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference communityCollection = db.collection("community")
                    .document(uid)
                    .collection("storys");


            //현재 communitystory의 document id 가져오기
            communityCollection
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // 문서가 존재하는 경우
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String lastestDocumentName = document.getId();
                                currentDocName = lastestDocumentName;

                                DocumentReference communityDocument = db
                                        .collection("community")
                                        .document(uid).collection("storys")
                                        .document(lastestDocumentName);

                                Map<String, Object> communityDocMap = new HashMap<>();
                                communityDocMap.put(FirebaseId.title, title);
                                communityDocMap.put(FirebaseId.peopleCurrent, prePeople);
                                communityDocMap.put(FirebaseId.peopleAll, allPeople);
                                communityDocMap.put(FirebaseId.kakaoLink, kakaoLink);
                                communityDocMap.put(FirebaseId.moreExplain, moreExp);

                                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(kakaoLink) || TextUtils.isEmpty(moreExp)) {
                                    communityDocument.delete();
                                    Log.d("firebase", "입력하지 않아 삭제되었습니다.");
                                    dialog.dismiss();
                                } else {
                                    communityDocument
                                            .update(communityDocMap)
                                            .addOnSuccessListener(documentReference -> {
                                                // 성공
                                                Toast.makeText(getContext(), "minininnimini", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                updateUI();

                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error updating document", e);
                                            });

                                    DocumentReference communityTimeStamp = db.collection("community")
                                            .document(uid);

                                    db.collection("user").document(uid)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    nickname[0] = documentSnapshot.getString(FirebaseId.nickname);
                                                    profileImage[0] = documentSnapshot.getString("profileImage");


                                                    Map<String, Object> tsmp = new HashMap<>();
                                                    tsmp.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
                                                    tsmp.put(FirebaseId.nickname, nickname[0]);
                                                    tsmp.put("profileImage", profileImage[0]);

                                                    communityTimeStamp
                                                            .set(tsmp);
                                                }
                                            });


                                }
                            } else {
                                // 문서가 없는 경우
                                Log.d("Firestore", "No documents found.");
                            }
                        } else {
                            // 작업 실패
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    });

        });
    }

    private void updateUI() {
         FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
         Fragment_community_story fragmentCommunityStory = new Fragment_community_story();

         Bundle bundle = new Bundle();
         bundle.putString(FirebaseId.place, binding.communityAddPlace.getText().toString());
         bundle.putString(FirebaseId.date, binding.communityAddDate.getText().toString());
         bundle.putString(FirebaseId.time, binding.communityAddTime.getText().toString());
         bundle.putString(FirebaseId.title, binding2.communityInnerTitle.getText().toString());
         bundle.putString(FirebaseId.peopleCurrent, binding2.communityInnerPresentpeople.getText().toString());
         bundle.putString(FirebaseId.peopleAll, binding2.communityInnerAllpeople.getText().toString());
         bundle.putString(FirebaseId.kakaoLink, binding2.communityInnerKakaoLink.getText().toString());
         bundle.putString(FirebaseId.moreExplain, binding2.communityInnerMoreExplain.getText().toString());
         bundle.putString(FirebaseId.currentDocId, currentDocName);
         fragmentCommunityStory.setArguments(bundle);

         callAddCommunityStoryMethod();
         transaction.replace(R.id.containers, fragmentCommunityStory);
         transaction.commit();

    }
}