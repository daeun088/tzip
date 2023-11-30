package com.example.tzip;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity implements ModalBottomSheet.OnDismissListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Button button = findViewById(R.id.show_bottom_sheet_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
                modalBottomSheet.setOnDismissListener(ThirdActivity.this);
                modalBottomSheet.show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public void onDismis(boolean isSwitchOn) {
        TextView resultTextView = findViewById(R.id.result);
        resultTextView.setText(isSwitchOn ? "ON" : "OFF");
    }
}