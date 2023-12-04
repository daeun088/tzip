package com.example.tzip;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.tzip.databinding.FragmentRecordWritingBinding;
import com.example.tzip.databinding.FregmentRecordWriteInnerBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RecordWriting extends Fragment {
    FragmentRecordWritingBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private BottomSheetDialog dialog; // 바텀시트용 dialog 객체 <민>

    public RecordWriting() {
        // Required empty public constructor
    }
    public static RecordWriting newInstance(String param1, String param2) {
        RecordWriting fragment = new RecordWriting();
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
        // 기존 코드에서 수정: binding 객체 초기화
        binding = FragmentRecordWritingBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String date = bundle.getString(FirebaseId.date, "");
            String place = bundle.getString(FirebaseId.place, "");
            binding.tripDate.setText("여행일시 - " + date);
            binding.tripPlace.setText("여행장소 - " + place);
        }//add에서 입력된 데이터 불러오기

        binding.recordMainImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            // Get a default Storage bucket
        });

        binding.galleryBtn.setOnClickListener(v -> {
            //갤러리에서 사진 받아오면 현재 입력 중인 여행 블럭에 사진 추가한 후
            //밑에 다시 입력할 수 있는 뷰 띄우기
        });

        // 바텀시트 띄우기 여기서부터 <민>
        dialog = new BottomSheetDialog(requireContext()); // requireContext 써도 되려나

        binding.addScheduleBtn.setOnClickListener(v -> {
            View contentView = RecordWriting.this.getLayoutInflater().inflate(R.layout.fregment_record_write_inner, null);
            dialog.setContentView(contentView);

            attachListenerToContentView(contentView);

            dialog.show();
        });

        // 여기까지 <민>

        return binding.getRoot();
    }

    private void attachListenerToContentView(View contentView) {
    } // 이너페이지 안에 있는 애들 리스너 달아주기 <민>

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 선택된 이미지를 ImageView에 설정
            Uri imageUri = data.getData();
            binding.recordPicture.setImageURI(imageUri);

            // ImageView의 Drawable이 BitmapDrawable인지 확인하고 null이 아닌 경우에만 업로드
            Drawable drawable = binding.recordPicture.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                // 선택된 이미지를 Firebase Storage에 업로드
                uploadImageToFirebaseStorage(bitmap);
            } else {
                // 다른 Drawable 타입이거나 null인 경우에 대한 처리
                Log.e("daeun", "ImageView의 Drawable이 유효하지 않습니다.");
            }
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        // Get a default Storage bucket
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Points to the root reference
        StorageReference storageRef = storage.getReference();

        // Create a reference for a new image
        StorageReference mountainImagesRef = storageRef.child(getPath("jpg"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //0-100
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("daeun", "이미지뷰의 이미지 업로드 실패", exception);
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
            Log.d("daeun", "이미지뷰의 이미지 업로드 성공");
        });
    }


    public String getTitle(){
        return binding.recordTitle.getText().toString();
    }

    private String getPath(String extension) {
        String uid = getUidOfCurrentUser();

        String dir = (uid != null) ? uid : "public";

        String fileName = (uid != null) ? (uid + "_" + System.currentTimeMillis() + "." + extension)
                : ("anonymous" + "_" + System.currentTimeMillis() + "." + extension);

        return dir + "/" + fileName;
    }

    private boolean hasSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null ? true : false;
    }

    private String getUidOfCurrentUser() {
        return hasSignedIn() ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }



}