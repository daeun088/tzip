package com.example.tzip;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import com.example.tzip.databinding.ScheduleMainRecyclerviewBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;




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

    Fragment_schedule_plan fragmentSchedulePlan;

    FragmentScheduleBinding binding;

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

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            list.add("Item=" + i);
        }

        binding.scheduleBlockList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.scheduleBlockList.setAdapter(new Fragment_schedule.MyAdapter(list));
        binding.scheduleBlockList.addItemDecoration(new Fragment_schedule.MyItemDecoration());

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ScheduleMainRecyclerviewBinding binding;

        private MyViewHolder(ScheduleMainRecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.scheduleBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
                    callSchedulePlanMethod();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentSchedulePlan = new Fragment_schedule_plan();
                    transaction.replace(R.id.containers, fragmentSchedulePlan).commit();
                }
            });
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> list;

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