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

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentCommunityBinding;
import com.example.tzip.databinding.FragmentHomeBinding;
import com.example.tzip.databinding.FragmentRecordAddBinding;
import com.example.tzip.databinding.ItemCommunityInnerBinding;
import com.example.tzip.databinding.ItemHomeCardBinding;
import com.example.tzip.databinding.ItemHomeListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_home extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    int itemCount;

    FragmentHomeBinding binding;
    ItemHomeListBinding binding2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Fragment_home";



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

    private void callSchedulePlanMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForSchedulePlan(); // 액티비티의 메서드 호출
            activity.post_id = R.id.schedule;
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

        List<HomeDataSet> list = new ArrayList<>();
        List<CommunityDataSet> clist = new ArrayList<>();

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
            callCommunityMethod();
            Fragment_community community = new Fragment_community();
            transaction.replace(R.id.containers, community);
            transaction.commit();
        });

        // see all

        //카드 가지러옴
        final String[] tempT = new String[1];
        final String[] tempP = new String[1];
        final String[] tempL = new String[1];
        final String[] tempI = new String[1];
        final String[] tempH = new String[1];
        final String[] tempD = new String[1];

        db.collection("schedule")
                .document(getUidOfCurrentUser()).collection("schedules")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document2 : task.getResult()) {
                                Log.d(TAG, document2.getId());
                                tempT[0] = document2.getString(FirebaseId.title);
                                tempL[0] = document2.getString(FirebaseId.place);
                                tempI[0] = document2.getString(FirebaseId.imageUrl);
                                tempD[0] = document2.getId();

                                list.add(new HomeDataSet(tempT[0], tempL[0], tempI[0]));
                                Log.d(TAG, "title>> " + tempT[0] + " loc>> " + tempL[0] + " img>> " + tempI[0]);
                                binding.recyclerviewHomeCard.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
                                binding.recyclerviewHomeCard.setAdapter(new CardAdapter(list));
                                binding.recyclerviewHomeCard.addItemDecoration(new MyItemDecoration());
                            }
                        }
                    }
                });
        //카드 가지러옴

        //리스트 가지러옴

        final int listNum = 4;

        final String[] ctempT = new String[1];
        final String[] ctempL = new String[1];
        final String[] ctempI = new String[1];
        final String[] ctempH = new String[1];
        final String[] ctempD = new String[1];
        final String[] ctempN = new String[1];
        final String[] ctempP = new String[1];



        db.collection("community")
                .whereNotEqualTo(FieldPath.documentId(), getUidOfCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId());
                                String tmpN;
                                String tmpP;
                                tmpN = document.getString(FirebaseId.nickname);
                                tmpP = document.getString("profileImage");
                                CollectionReference getBlockSrd = db.collection("community")
                                        .document(document.getId())
                                        .collection("storys");
                                getBlockSrd.orderBy("timestamp").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                        Log.d(TAG, document2.getId());
                                                        String tmpT;
                                                        String tmpL;
                                                        String tmpH;
                                                        String tmpD;
                                                        tmpT = document2.getString(FirebaseId.title);
                                                        tmpL = document2.getString(FirebaseId.place);
                                                        tmpH = document2.getString(FirebaseId.peopleAll);
                                                        tmpD = document2.getId();

                                                        clist.add(new CommunityDataSet(tmpT, tmpN, tmpL, tmpP));
//                                                        Log.d(TAG, "title>> " + tempT[0]+" per>> " + tempP[0]+ " loc>> " + tempL[0] + " img>> " + tempI[0] + " people>> " + tempH[0]);
                                                        binding.homeCommunityList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                                                        binding.homeCommunityList.setAdapter(new CommunityAdapter(clist));
                                                        binding.homeCommunityList.addItemDecoration(new MyItemDecoration());
                                                    }
                                                }
                                            }
                                        });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        final int[] count = {0};


        db.collection("record").document(getUidOfCurrentUser()).collection("records")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            count[0] = task.getResult().size();
                            binding.homeRecordCount.setText(Integer.toString(count[0]) + "개");
                            binding.homeProgressbar.setIndeterminate(false);
                            binding.homeProgressbar.setProgress(count[0]);

                        }
                    }
                });





        return binding.getRoot();
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.set(0,1,0,0);
        }
    }

    // 카드 뷰홀더

    private class CardViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeCardBinding binding;

        private CardViewHolder(ItemHomeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.itemHomeCard.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    int pos = getBindingAdapterPosition();

                    // 사용자의 uid로 기록 서브컬렉션에 접근
                    CollectionReference schedulesCollection = db
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
                                    String DocumentID = loadedData.getString(FirebaseId.documentId);
                                    itemList.add(DocumentID);

                                }

                                // 클릭한 아이템의 인덱스(pos)와 itemList의 인덱스(index)를 비교하여 같으면 데이터 저장
                                for (int index = 0; index < itemList.size(); index++) {
                                    Fragment_schedule_plan fragmentSchedulePlan;
                                    if (pos == index) {
                                        // 클릭한 아이템의 데이터를 itemList에서 가져와 저장하는 코드
                                        String clickedItemData = itemList.get(index);
                                        // Bundle을 이용하여 데이터 전달
                                        Bundle bundle = new Bundle();
                                        bundle.putString("schedule", clickedItemData);
                                        fragmentSchedulePlan = new Fragment_schedule_plan();
                                        fragmentSchedulePlan.setArguments(bundle);
                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.containers, fragmentSchedulePlan).commit();
                                        callSchedulePlanMethod();
                                    }
                                }
                            });
                    // 클릭 이벤트 처리

                }
            });
        }
    }

    // 카드 뷰홀더

    // 카드 어댑터
    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private List<HomeDataSet> list;

        private CardAdapter(List<HomeDataSet> list) {
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
            HomeDataSet HDS = list.get(position);
            String title = HDS.getTitle();
            String place = HDS.getLocation();
            String img = HDS.getImage();

            holder.binding.homeCardTitle.setText(title);
            holder.binding.homeCardTitle.setSelected(true);
            holder.binding.homeCardLocation.setText(place);
            if (img != null && !img.isEmpty()) {
                Glide.with(holder.binding.homeCardImage.getContext())
                        .load(img)
                        .into(holder.binding.homeCardImage);
            } else {
                // 이미지 URL이 없을 때 디폴트 이미지 설정
                Glide.with(holder.binding.homeCardImage.getContext())
                        .load(R.drawable.schedule_example_pic) // 여기서 R.drawable.default_image는 디폴트 이미지의 리소스 ID입니다.
                        .into(holder.binding.homeCardImage);
            }

            setOnClickListenerToItem(holder.binding.itemHomeCard);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    // 카드 어댑터



    private void setOnClickListenerToItem(View view) {
        ItemHomeCardBinding binding1 = ItemHomeCardBinding.bind(view);

    }

    // 리스트 뷰홀더
    private class CommunityViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeListBinding binding;

        private CommunityViewHolder(ItemHomeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // 리스트 뷰홀더

    // 리스트 어댑터
    private class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder> {
        private List<CommunityDataSet> list;

        private CommunityAdapter(List<CommunityDataSet> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeListBinding binding = ItemHomeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CommunityViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
            CommunityDataSet CDS = list.get(position);
            String title = CDS.getTitle();
            String place = CDS.getLocation();
            String img = CDS.getImage();
            String name = CDS.getName();

            holder.binding.homeListTitle.setText(title);
            holder.binding.homeListLocation.setText(place);
            holder.binding.homeListName.setText(name);
            if (img != null && !img.isEmpty()) {
                Glide.with(holder.binding.homeListImage.getContext())
                        .load(img)
                        .into(holder.binding.homeListImage);
            } else {
                // 이미지 URL이 없을 때 디폴트 이미지 설정
                Glide.with(holder.binding.homeListImage.getContext())
                        .load(R.drawable.schedule_example_pic) // 여기서 R.drawable.default_image는 디폴트 이미지의 리소스 ID입니다.
                        .into(holder.binding.homeListImage);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    // 리스트 어댑터


    // 카드 아이템
    private class HomeDataSet {
        private String image;
        private String title;
        private String location;

        // 생성자 함수
        public HomeDataSet(String title, String location, String image) {
            this.image = image;
            this.title = title;
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation() {
            return location;
        }
    }

    private class CommunityDataSet {
        private String image;
        private String title;
        private String location;
        private String name;

        // 생성자 함수
        public CommunityDataSet(String title, String name, String location, String image) {
            this.image = image;
            this.title = title;
            this.location = location;
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }
    }

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }
}