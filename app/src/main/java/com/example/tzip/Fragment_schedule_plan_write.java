package com.example.tzip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class Fragment_schedule_plan_write extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private EditTextAdapter editTextAdapter;

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

        recyclerView = view.findViewById(R.id.schedule_plan_write_recyclerView);
        editTextAdapter = new EditTextAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(editTextAdapter);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.board_write);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAdapter.addNewItem();
            }
        });

        return view;
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