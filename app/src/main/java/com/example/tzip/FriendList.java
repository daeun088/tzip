package com.example.tzip;

import android.content.ClipData;
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

import com.example.tzip.databinding.FragmentFriendListBinding;
import com.example.tzip.databinding.FragmentFriendRequestBinding;
import com.example.tzip.databinding.ItemFriendListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentFriendListBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendList.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendList newInstance(String param1, String param2) {
        FriendList fragment = new FriendList();
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
        binding = FragmentFriendListBinding.inflate(inflater, container, false);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Item=" + i);
        }

        binding.friendNum.setText("친구 "+list.size());

        binding.friendList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.friendList.setAdapter(new FriendList.MyAdapter(list));
        binding.friendList.addItemDecoration(new FriendList.MyItemDecoration());

        binding.addFriendBtn.setOnClickListener( v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment_friend_request fragmentFriendRequest = new Fragment_friend_request();
            transaction.replace(R.id.containers, fragmentFriendRequest);
            transaction.commit();
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendListBinding binding;

        private MyViewHolder(ItemFriendListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<FriendList.MyViewHolder> {
        private List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }
        @NonNull
        @Override
        public FriendList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFriendListBinding binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.getContext()) ,parent, false);
            return new FriendList.MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendList.MyViewHolder holder, int position) {
            String text = list.get(position);
            holder.binding.friendName.setText(text);
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
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            Drawable dr = ResourcesCompat.getDrawable(getResources(), R.drawable.setting, null);

            int width = parent.getWidth();
            int height = parent.getHeight();
            int drWidth = dr.getIntrinsicWidth();
            int drHeight = dr.getIntrinsicHeight();
            int left = width / 2 - drWidth / 2;
            int top = height / 2 - drHeight / 2;

            c.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.setting), left, top, null);
        }
    }
}