package com.example.tzip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class RecordItem {
    private String date;
    private String time;
    private String blockTitle;

    public RecordItem() {
        // 기본 생성자
    }

    public RecordItem(String date, String time, String blockTitle) {
        this.date = date;
        this.time = time;
        this.blockTitle = blockTitle;
    }

    public Date getDateTimeObject() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault());
            String dateTimeString = date + " " + time;
            return dateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getter 및 Setter 메서드 추가
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBlockTitle() {
        return blockTitle;
    }

    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }
}
