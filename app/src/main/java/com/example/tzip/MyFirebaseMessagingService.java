package com.example.tzip;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("FCM Token", s);
        sendRegistrationToServer(s);
        saveTokenToPreferences(s);

    }

    private void saveTokenToPreferences(String token) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("FCM_TOKEN", token);
        editor.apply();
    }

    public void sendRegistrationToServer(String token) {
        // 여기에서 서버에 토큰을 전송하는 로직을 작성
        // 예를 들어, 사용자의 데이터베이스 레코드에 토큰을 저장
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("userToken").document(currentUserId);

        // 'fcmTokens' 필드를 직접 설정
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put(FirebaseId.token, token);

        userDocRef.set(tokenMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("FCM Token", "Token successfully added to Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM Token", "Error adding token to Firestore", e);
                });
    }

    public void showNotification(String title, String message) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        Intent intent = new Intent(this, Fragment_notification.class);
        String channel_id = "CHN_ID";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), channel_id);

        builder = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.tziplogo);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //알림 채널이 필요한 안드로이드 버전을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "CHN_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //알림 표시 !
        notificationManager.notify(0, builder.build());
    }

}
