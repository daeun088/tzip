package com.example.tzip;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.tzip.databinding.FragmentScheduleBinding;
import com.example.tzip.databinding.FragmentSchedulePlanBinding;
import com.example.tzip.databinding.ScheduleMainRecyclerviewBinding;
import com.example.tzip.databinding.SchedulePlanRecyclerviewBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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

    FragmentSchedulePlanBinding binding;

    Fragment_schedule fragmentSchedule;
    Fragment_schedule_plan_write fragmentSchedulePlanWrite;

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

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
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
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<Fragment_schedule_plan.MyViewHolder> {
        private List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment_schedule_plan.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SchedulePlanRecyclerviewBinding binding = SchedulePlanRecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new Fragment_schedule_plan.MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull Fragment_schedule_plan.MyViewHolder holder, int position) {
            String text = list.get(position);
//            holder.binding.schedule_block_title.setText(text); //고쳐야 할 부분
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
}