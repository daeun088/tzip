package com.example.tzip;

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
