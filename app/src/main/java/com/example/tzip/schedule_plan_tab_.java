package com.example.tzip;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class schedule_plan_tab_ extends FragmentActivity {

    TabLayout tabs;

    Fragment_schedule_plan fragment_schedule_plan; //임시로 생성해둔 fragment, 구현시에 Fragment를 추가할 방법을 생각해야할듯
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation_bar_test_code);

        fragment_schedule_plan = new Fragment_schedule_plan();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_schedule_plan).commit();

        tabs = findViewById(R.id.tab_layout);
        tabs.addTab(tabs.newTab().setText("친구")); //setText는 임시로 해둔것, 이후에 tab만들 때 달력하고 연동해서 선택한 날짜로 setText하면 될듯
        tabs.addTab(tabs.newTab().setText("채팅"));
        tabs.addTab(tabs.newTab().setText("더보기"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0)
                    selected = fragment_schedule_plan;
                else if(position == 1)
                    selected = fragment_schedule_plan;
                else if(position == 2)
                    selected = fragment_schedule_plan; //여행 기간이 설정되면 그 일수만큼 position과 fragment가 추가되도록 하면 될듯
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}