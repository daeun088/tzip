package com.example.tzip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment_schedule_plan_write extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private EditTextAdapter editTextAdapter;

    private EditText scheduleEditText;

    private  EditText schedulePlanTitle;
    private ImageButton regButton;

    String scheduleClickedItemData;

    String schedulePlanClickedItemData;


    private FirebaseFirestore schedulePlanWriteDB = FirebaseFirestore.getInstance();


    public Fragment_schedule_plan_write() {
        // Required empty public constructor
    }

    public static Fragment_schedule_plan_write newInstance(String param1, String param2) {
        Fragment_schedule_plan_write fragment = new Fragment_schedule_plan_write();
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
        View view = inflater.inflate(R.layout.fragment_schedule_plan_write, container, false);

        scheduleEditText = view.findViewById(R.id.schedule_plan_write_page);
        schedulePlanTitle = view.findViewById(R.id.schedule_plan_write_title);
        regButton = view.findViewById(R.id.reg_btn);



        if (getArguments() != null) {
            schedulePlanClickedItemData = getArguments().getString("schedule_plan");
            // 읽어온 데이터를 이용하여 원하는 작업 수행
        }

        if (getArguments() != null) {
            scheduleClickedItemData = getArguments().getString("schedule");
            // 읽어온 데이터를 이용하여 원하는 작업 수행
        }

        loadScheduleFromFirebase();
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
                Toast.makeText(getContext(), "내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveDataToFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String write_page = scheduleEditText.getText().toString();
        String write_title = schedulePlanTitle.getText().toString();
        CollectionReference schedulesCollection = schedulePlanWriteDB
                .collection("schedule")
                .document(uid)
                .collection("schedules")
                .document(scheduleClickedItemData)
                .collection("schedulePlanBlocks")
                .document(schedulePlanClickedItemData)
                .collection("schedulePlanWritePages");

        Map<String, Object> schedulePlanMap = new HashMap<>();
        schedulePlanMap.put(FirebaseId.wrtiepage, write_page);
        schedulePlanMap.put(FirebaseId.wrtietitle, write_title);
        schedulePlanMap.put(FirebaseId.timestamp, FieldValue.serverTimestamp());

        schedulesCollection.add(schedulePlanMap);
    }

    protected void loadScheduleFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference schedulesCollection = schedulePlanWriteDB
                .collection("schedule")
                .document(uid)
                .collection("schedules")
                .document(scheduleClickedItemData)
                .collection("schedulePlanBlocks")
                .document(schedulePlanClickedItemData)
                .collection("schedulePlanWritePages");

        schedulesCollection.orderBy(FirebaseId.timestamp, Query.Direction.ASCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String loadedData = document.getString(FirebaseId.wrtiepage);
                    String loadedData2 = document.getString(FirebaseId.wrtietitle);
                    scheduleEditText.setText(loadedData);
                    schedulePlanTitle.setText(loadedData2);
                }
            } else {
                // Handle errors
            }
        });
    }



    // 새로운 아이템을 추가하기 위한 어댑터
    private class EditTextAdapter extends RecyclerView.Adapter<EditTextAdapter.ViewHolder> {

        private List<String> itemList;

        public EditTextAdapter() {
            itemList = new ArrayList<>();
        }

        public void addNewItem() {
            itemList.add("");
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_plan_write_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Drawable 추가
            holder.editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_plan_write_list_icon, 0, 0, 0);
            holder.editText.setCompoundDrawablePadding(16); // drawables 간 간격 조절

            // 원하는 설정을 추가하세요.
            holder.editText.setHint("새로운 텍스트 입력");
            holder.editText.setTextSize(20);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EditText editText;

            public ViewHolder(View itemView) {
                super(itemView);
                editText = itemView.findViewById(R.id.editText);
            }
        }

    }
}