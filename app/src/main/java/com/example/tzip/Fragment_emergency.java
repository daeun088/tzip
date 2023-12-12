package com.example.tzip;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tzip.databinding.FragmentEmergencyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Fragment_emergency extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    FragmentEmergencyBinding binding;

    EditText emergency_message;

    private FirebaseFirestore EmergencyDB = FirebaseFirestore.getInstance();

    public Fragment_emergency() {
        // Required empty public constructor
    }

    public static Fragment_emergency newInstance(String param1, String param2) {
        Fragment_emergency fragment = new Fragment_emergency();
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
        // Inflate the layout for this fragment
        binding = FragmentEmergencyBinding.inflate(inflater, container, false);

        emergency_message = binding.emergencyMessage;

        binding.emergencySettingBtn.setOnClickListener( v -> {

            saveDataToFirebase();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment_mypage fragmentMypage = new Fragment_mypage();
            transaction.replace(R.id.containers, fragmentMypage);
            transaction.commit();
            Toast.makeText(getContext(), "긴급메세지 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        });
        return binding.getRoot();
    }
    private void saveDataToFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String EmergencyMessage = emergency_message.getText().toString();
        CollectionReference schedulesCollection = EmergencyDB
                .collection("Emergency")
                .document(uid)
                .collection("EmergencyMessage");

        Map<String, Object> EmergencyMap = new HashMap<>();
        EmergencyMap.put(FirebaseId.EmergencyMessage, EmergencyMessage);
        EmergencyMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());

        schedulesCollection.add(EmergencyMap);
    }

    protected void loadFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = EmergencyDB
                .collection("Emergency")
                .document(uid)
                .collection("EmergencyMessage");

        schedulesCollection.orderBy(FirebaseId.timestamp, Query.Direction.ASCENDING)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String loadedData = document.getString(FirebaseId.EmergencyMessage);
                            emergency_message.setText(loadedData);
                        }
                    } else {
                        // Handle errors
                    }
                });
    }


}