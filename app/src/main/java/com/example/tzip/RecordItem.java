package com.example.tzip;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Comparator;
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

    public static class ItemSort implements Comparator<RecordItem> {
        @Override
        public int compare(RecordItem o1, RecordItem o2) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);

            try {
                if (o1.getDate() == null || o2.getDate() == null) {
                    // 날짜 정보가 null인 경우 예외 처리 또는 특정 로직 수행
                    return 0;
                }

                Date date1 = dateFormat.parse(o1.getDate());
                Date time1 = o1.getTime() != null ? timeFormat.parse(o1.getTime()) : new Date();

                Date date2 = dateFormat.parse(o2.getDate());
                Date time2 = o2.getTime() != null ? timeFormat.parse(o2.getTime()) : new Date();

                // 날짜와 시간을 하나의 Date 객체로 합칩니다.
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date1);
                calendar1.set(Calendar.HOUR_OF_DAY, time1.getHours());
                calendar1.set(Calendar.MINUTE, time1.getMinutes());

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(date2);
                calendar2.set(Calendar.HOUR_OF_DAY, time2.getHours());
                calendar2.set(Calendar.MINUTE, time2.getMinutes());

                // 날짜 및 시간 비교
                int dateComparison = calendar1.compareTo(calendar2);

                // 날짜가 같은 경우 시간으로 비교
                if (dateComparison == 0) {
                    return time1.compareTo(time2);
                } else {
                    return dateComparison;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0; // 오류 시 0 반환 혹은 다른 방식으로 처리
            }
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
