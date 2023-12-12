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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.tzip.databinding.FragmentCommunityBinding;
import com.example.tzip.databinding.FragmentMypageBinding;
import com.example.tzip.databinding.ItemCommunityInnerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
        final String[] tempI = new String[1];
        final String[] tempH = new String[1];
        final String[] tempD = new String[1];

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
                                                        tempT[0] = document2.getString(FirebaseId.title);
                                                        tempP[0] = document2.getString(FirebaseId.date);
                                                        tempL[0] = document2.getString(FirebaseId.place);
                                                        tempI[0] = document2.getString(FirebaseId.imageUrl);
                                                        tempH[0] = document2.getString(FirebaseId.peopleAll);
                                                        tempD[0] = document2.getId();

                                                        list.add(new CommunityDataSet(tempT[0], tempP[0], tempL[0], tempI[0], tempH[0], tempD[0], document.getId()));
                                                        Log.d(TAG, "title>> " + tempT[0]+" per>> " + tempP[0]+ " loc>> " + tempL[0] + " img>> " + tempI[0] + " people>> " + tempH[0]);
                                                        binding.serchList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                                                        binding.serchList.setAdapter(new MyAdapter(list));
                                                        binding.serchList.addItemDecoration(new MyItemDecoration());
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
            String img = DS.getImg();
            String people = DS.getPeople();
            String docId = DS.getDocID();
            String uid = DS.getuID();

            holder.binding.communityBlockTitle.setText(title);
            holder.binding.communityBlockPeriod.setText(period);
            holder.binding.communityBlockLocation.setText(location);
            // 이미지 넣기
            if (img != null && !img.isEmpty()) {
                Glide.with(holder.binding.communityBlockPic.getContext())
                        .load(img)
                        .into(holder.binding.communityBlockPic);
            } else {
                // 이미지 URL이 없을 때 디폴트 이미지 설정
                Glide.with(holder.binding.communityBlockPic.getContext())
                        .load(R.drawable.schedule_example_pic) // 여기서 R.drawable.default_image는 디폴트 이미지의 리소스 ID입니다.
                        .into(holder.binding.communityBlockPic);
            }
            // 이미지 넣기
            holder.binding.communityBlockParticipant.setText("인원 수 " + people + "명");

            holder.binding.communityBlock.setOnClickListener(v -> {
                updateUI(holder.binding.communityBlock, docId, uid, img);
            });

        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + list.size());
            return list.size();
        }
    }

    private void updateUI(View view, String docId, String uid, String imgUrl) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment_community_Read fragmentCommunityRead = new Fragment_community_Read();
        DocumentReference dRef = db.collection("community")
                .document(uid)
                .collection("storys")
                .document(docId);

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseId.place, documentSnapshot.getString(FirebaseId.place));
                bundle.putString(FirebaseId.date, documentSnapshot.getString(FirebaseId.date));
                bundle.putString(FirebaseId.time,  documentSnapshot.getString(FirebaseId.time));
                bundle.putString(FirebaseId.title, documentSnapshot.getString(FirebaseId.title));
                bundle.putString(FirebaseId.peopleCurrent, documentSnapshot.getString(FirebaseId.peopleCurrent));
                bundle.putString(FirebaseId.peopleAll, documentSnapshot.getString(FirebaseId.peopleAll));
                bundle.putString(FirebaseId.kakaoLink, documentSnapshot.getString(FirebaseId.kakaoLink));
                bundle.putString(FirebaseId.moreExplain, documentSnapshot.getString(FirebaseId.moreExplain));
                bundle.putString(FirebaseId.imageUrl, documentSnapshot.getString(FirebaseId.imageUrl));
                fragmentCommunityRead.setArguments(bundle);

                callAddCommunityStoryMethod();
                transaction
                        .replace(R.id.containers, fragmentCommunityRead)
                        .commit();
            }
        });


    }

    private void callAddCommunityStoryMethod() {
        if(getActivity() instanceof nevigation_bar_test_code) {
            nevigation_bar_test_code activity = (nevigation_bar_test_code) getActivity();
            activity.setTollbarForCommunityStory();
        }
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int index = parent.getChildAdapterPosition(view) + 1;
            outRect.set(0,0,0,5);
        }
    }

    private class CommunityDataSet {
        String title;
        String period;
        String location;
        String img;
        String people;
        String docID;
        String uID;
        private CommunityDataSet(String src_title, String src_per, String src_loc, String src_img, String src_peo, String src_docid, String src_uid) {
            title = src_title;
            period = src_per;
            location = src_loc;
            img = src_img;
            people = src_peo;
            docID = src_docid;
            uID = src_uid;

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

        public String getImg() {
            return img;
        }

        public String getPeople() {
            return people;
        }

        public String getDocID() {
            return docID;
        }

        public String getuID() {
            return uID;
        }
    }
}