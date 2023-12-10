package com.example.tzip;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tzip.databinding.FragmentCommunityBinding;
import com.example.tzip.databinding.FragmentHomeBinding;
import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.example.tzip.databinding.ItemCommunityInnerBinding;
import com.example.tzip.databinding.ItemHomeCardBinding;
import com.example.tzip.databinding.ItemHomeListBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Fragment_home extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    FragmentHomeBinding binding;
    ItemHomeListBinding binding2;


    // 리사이클러뷰 가져오기
    TextView dataPickerText;

    public Fragment_home() {
        // Required empty public constructor
    }

    // 카드 밑에 버튼들을 위함

    private void callScheduleMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForSchedule(); // 액티비티의 메서드 호출
        }
    }

    private void callRecordMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForRecord(); // 액티비티의 메서드 호출
        }
    }

    private void callCommunityMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForCommunity(); // 액티비티의 메서드 호출
        }
    }

    private void callMypageMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForMypage(); // 액티비티의 메서드 호출
        }
    }

    private void callAlertMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForEmergencyMessage(); // 액티비티의 메서드 호출
        }
    }

    //카드 밑에 버튼들을 위함

    public static Fragment_home newInstance() {
        return new Fragment_home();
    }


    public static Fragment_home newInstance(String param1, String param2) {
        Fragment_home fragment = new Fragment_home();
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

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Item=" + i);
        }

        binding.recyclerviewHomeCard.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.recyclerviewHomeCard.setAdapter(new CardAdapter(list));
        binding.recyclerviewHomeCard.addItemDecoration(new MyItemDecoration());

        // 카드 밑 메뉴 버튼

        binding.homeGoSchedule.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callScheduleMethod();
            Fragment_schedule schedule = new Fragment_schedule();
            transaction.replace(R.id.containers, schedule);
            transaction.commit();
        });

        binding.homeGoRecord.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callRecordMethod();
            Fragment_record record = new Fragment_record();
            transaction.replace(R.id.containers, record);
            transaction.commit();
        });

        binding.homeGoCommunity.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callCommunityMethod();
            Fragment_community community = new Fragment_community();
            transaction.replace(R.id.containers, community);
            transaction.commit();
        });

        binding.homeGoMypage.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callMypageMethod();
            Fragment_mypage mypage = new Fragment_mypage();
            transaction.replace(R.id.containers, mypage);
            transaction.commit();
        });

        binding.homeGoAlert.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callAlertMethod();
            Fragment_emergency emergency = new Fragment_emergency();
            transaction.replace(R.id.containers, emergency);
            transaction.commit();
        });

        // 카드 밑 메뉴 버튼

        // see all
        binding.homeGoSeeall.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callScheduleMethod();
            Fragment_community community = new Fragment_community();
            transaction.replace(R.id.containers, community);
            transaction.commit();
        });

        // see all

        return binding.getRoot();
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
    }

    // 카드 뷰홀더

    private class CardViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeCardBinding binding;

        private CardViewHolder(ItemHomeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // 카드 뷰홀더

    // 카드 어댑터
    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private List<String> list;

        private CardAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeCardBinding binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CardViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            String text = list.get(position);
            holder.binding.homeCardItem.setText(text);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    // 카드 어댑터

    // 리스트 어댑터
    private class ListAdapter extends BaseAdapter {

        List<HomeListItemData> items = null;
        Context context;

        public ListAdapter(Context context, List<HomeListItemData> items) {
            this.items = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public HomeListItemData getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                binding2 = ItemHomeListBinding.inflate(inflater, parent, false);
                binding2.getRoot().setTag(binding2);
            } else {
                binding2 = (ItemHomeListBinding) convertView.getTag();
            }

            // ListView의 Item을 구성하는 뷰 세팅
            HomeListItemData item = items.get(position);
            binding2.homeListImage.setImageResource(item.getImage());
            binding2.homeListName.setText(item.getName());
            binding2.homeListTitle.setText(item.getTitle());
            binding2.homeListLocation.setText(item.getLocation());

            // 설정한 binding.getRoot()를 반환해줘야 함
            return binding2.getRoot();

        }

        // 리스트 어댑터

        private class HomeListItemData {
            private int image;
            private String name;
            private String title;
            private String location;

            // 생성자 함수
            public HomeListItemData(int image, String name, String title, String location) {
                this.image = image;
                this.name = name;
                this.title = title;
                this.location = location;
            }

            public int getImage() {
                return image;
            }

            public String getName() {
                return name;
            }

            public String getTitle() {
                return title;
            }

            public String getLocation() {
                return location;
            }
        }
    }
}