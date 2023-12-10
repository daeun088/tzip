package com.example.tzip;

import android.content.Intent;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tzip.databinding.FragmentCommunityBinding;
import com.example.tzip.databinding.FragmentMypageBinding;
import com.example.tzip.databinding.ItemCommunityInnerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_community extends Fragment {

    FragmentCommunityBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "Fragment_community";

    private void callCommunityAddMethod() {
        if (getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setToolbarForCommunityAdd(); // 액티비티의 메서드 호출
        }
    }

    public Fragment_community() {
        // Required empty public constructor
    }

    public static Fragment_community newInstance(String param1, String param2) {
        Fragment_community fragment = new Fragment_community();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        List<CommunityDataSet> list = new ArrayList<>();

        binding.communityGetPeople.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            callCommunityAddMethod();
            CommunityAdd communityAdd = new CommunityAdd();
            transaction.replace(R.id.containers, communityAdd);
            transaction.commit();

        });

        final String[] tempT = new String[1];
        final String[] tempP = new String[1];
        final String[] tempL = new String[1];
        final String[] tt = new String[1];

        db.collection("community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId());
                                CollectionReference getBlockSrd = db.collection("community")
                                        .document(document.getId())
                                        .collection("storys");
                                getBlockSrd.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                        Log.d(TAG, document2.getId());
                                                        tempT[0] = document2.getString("title");
                                                        tempP[0] = document2.getString("date");
                                                        tempL[0] = document2.getString("place");
                                                        list.add(new CommunityDataSet(tempT[0], tempP[0], tempL[0]));
                                                        Log.d(TAG, "title>> " + tempT[0]+" per>> " + tempP[0]+ " loc>> " + tempL[0]);
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

        binding.serchList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.serchList.setAdapter(new MyAdapter(list));
        binding.serchList.addItemDecoration(new MyItemDecoration());
        return binding.getRoot();
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemCommunityInnerBinding binding;

        private MyViewHolder(ItemCommunityInnerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<CommunityDataSet> list;

        private MyAdapter(List<CommunityDataSet> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCommunityInnerBinding binding = ItemCommunityInnerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CommunityDataSet DS = list.get(position);
            String title = DS.getTitle();
            String period = DS.getPeriod();
            String location = DS.getLocation();

            holder.binding.communityBlockTitle.setText(title);
            holder.binding.communityBlockPeriod.setText(period);
            holder.binding.communityBlockLocation.setText(location);
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
    }

    private class CommunityDataSet {
        String title;
        String period;
        String location;
        private CommunityDataSet(String src_title, String src_per, String src_loc) {
            title = src_title;
            period = src_per;
            location = src_loc;
        }

        public String getTitle() {
            return title;
        }

        public String getPeriod() {
            return period;
        }

        public String getLocation() {
            return location;
        }
    }
}