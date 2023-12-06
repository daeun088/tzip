package com.example.tzip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                Date date1 = dateFormat.parse(o1.getDate());
                Date time1 = timeFormat.parse(o1.getTime());

                Date date2 = dateFormat.parse(o2.getDate());
                Date time2 = timeFormat.parse(o2.getTime());

                // 날짜 비교
                int dateComparison = date1.compareTo(date2);
                if (dateComparison == 0) {
                    // 날짜가 같을 때 시간 비교
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
