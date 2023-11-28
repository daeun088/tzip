package com.example.tzip;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SlidingDrawer;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}