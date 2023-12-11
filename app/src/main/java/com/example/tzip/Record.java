package com.example.tzip;

import android.net.Uri;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class Record implements Comparable<Record> {

    private String title;
    private String place;
    private String date;
    private String friendName;
    private String documentId;
    private String friendId;


    // 기존 생성자와 getter/setter 메서드는 여기에 포함됩니다.

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @PropertyName("contentImage")
    private String contentImage;

    @PropertyName("timestamp")
    private Date timestamp;

    public Record() {
        // 기본 생성자
    }

    // 필요한 생성자, Getter, Setter 추가...

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public Uri getContentImage() {
        return Uri.parse(contentImage);
    }
    public void setFriendId(String friendId) {this.friendId = friendId;}
    public String getFriendId() { return friendId;}

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Comparable 인터페이스를 구현하여 timestamp를 기준으로 내림차순 정렬
    @Override
    public int compareTo(Record otherRecord) {
        if (this.timestamp == null || otherRecord.timestamp == null) {
            return 0;
        }
        return otherRecord.timestamp.compareTo(this.timestamp);
    }
}
