package com.example.tzip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tzip.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore userDB = FirebaseFirestore.getInstance();
    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        binding.signupButton.setOnClickListener( v-> {
                String email = binding.email.getText().toString();
                String password = binding.pw.getText().toString();
                nickname = binding.nickname.getText().toString().trim();
                String passwordCheck = binding.pwCheck.getText().toString();

                if (!password.equals(passwordCheck)) {
                    // 비밀번호와 비밀번호 확인이 일치하지 않으면 토스트를 표시하고 회원가입 중단
                    Toast.makeText(getApplicationContext(), "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return; // 메소드 종료
                }

                signUp(email, password);
        });

        binding.BackButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put(FirebaseId.documentId, user.getUid());
                            userMap.put(FirebaseId.email, email);
                            userMap.put(FirebaseId.password, password);
                            userMap.put(FirebaseId.nickname, nickname);
                            userDB.collection(FirebaseId.user).document(user.getUid()).set(userMap, SetOptions.merge());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Log.d("signin", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, Login.class);
            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());

            startActivity(intent);
            finish();
        }
    }




}