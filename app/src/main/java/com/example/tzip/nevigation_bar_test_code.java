package com.example.tzip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

public class nevigation_bar_test_code extends AppCompatActivity {

    private Toolbar toolbar;
    private Button dynamicButton;

    Fragment_community fragmentCommunity;
    Fragment_schedule fragmentSchedule;
    Fragment_review fragmentReview;
    Fragment_home fragmentHome;
    Fragment_mypage fragmentMypage;
    Fragment_notification fragmentNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation_bar_test_code);

        fragmentSchedule = new Fragment_schedule();
        fragmentReview = new Fragment_review();
        fragmentCommunity = new Fragment_community();
        fragmentHome = new Fragment_home();
        fragmentMypage = new Fragment_mypage();
        fragmentNotification = new Fragment_notification();


        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);


        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containers); //현재 화면에 띄워진(containers에 속하는) fragment 찾기
        if (currentFragment instanceof Fragment_home) {

        } else if (currentFragment instanceof Fragment_community) {
            // Fragment_community에 대한 처리
        } else if (currentFragment instanceof Fragment_review) {
            // Fragment_review에 대한 처리
        } else if (currentFragment instanceof Fragment_schedule) {//현재 schedule만 fragment에 적혀있으므로 얘만 해놓음
            setToolbarContent("나의 여행 일정", false); //showBackButton이 true이면 back버튼이 나옴
            addButtonToToolbar("알림"); //해당 프레그먼트에 필요한 상단바의 버튼 종류 전달
        } else if (currentFragment instanceof Fragment_mypage) {
            // Fragment_mypage에 대한 처리
        } else if (currentFragment instanceof Fragment_notification) {
            setToolbarContent("알림", true); //showBackButton이 true이면 back버튼이 나옴
            addButtonToToolbar("하단 바 없애기"); //"하단 바 없애기"가 전달되면 버튼은 생성되지 않고 하단바만 사라지게 됨
            addButtonToToolbar("등록");//해당 프레그먼트에 필요한 상단바의 버튼 종류 전달
        }





        // 초기 상단바 설정
        setToolbarContent("나의 여행 일정", true);

        // 초기 버튼 추가
        addButtonToToolbar("동적 버튼");

        // 프래그먼트 초기화 및 화면 설정
        initFragments();

        BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
                    return true;
                } else if (item.getItemId() == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentCommunity).commit();
                    return true;
                } else if (item.getItemId() == R.id.review) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentReview).commit();
                    return true;
                } else if (item.getItemId() == R.id.schedule) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedule).commit();
                    return true;
                } else if (item.getItemId() == R.id.mypage) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.alert_Button) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
                    return true;
                    //이후에 fragmentMypage를 알림 페이지로 설정하면 됨
                }

                return false;
            }
        });
    }

    // 프래그먼트 초기화
    private void initFragments() {
        // 각각의 프래그먼트를 초기화하고 기본 화면으로 설정
        // (Fragment_home, Fragment_community, Fragment_review, Fragment_schedule, Fragment_mypage)
        // ... 각 프래그먼트의 초기 설정 코드 추가 ...

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, new Fragment_home()).addToBackStack(null).commit();
    }//뒤로가기 버튼 누를 시 이전 프레그먼트가 보여지게 설정

    // 상단바 내용 동적으로 변경
    private void setToolbarContent(String title, boolean showBackButton) {
        getSupportActionBar().setTitle(""); // 초기화

        // 새로운 TextView 생성
        TextView customTitle = new TextView(this);
        customTitle.setText(title);
        customTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22); // 크기 설정
        customTitle.setTextColor(Color.BLACK);
        customTitle.setGravity(Gravity.CENTER); // gravity 설정
        customTitle.setTypeface(null, Typeface.BOLD); // 스타일 설정

        // Toolbar에 새로운 TextView 추가
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
    }

    // 툴바에 버튼 추가
    private void addButtonToToolbar(String toolbar_identifier) {

        MenuItem dynamicButton = null;
        if(toolbar_identifier.equals("알림")){ //상단바에 알림 버튼이 필요한 경우
            dynamicButton = toolbar.getMenu().add(Menu.NONE, Menu.NONE, 1, "알림 버튼");
            dynamicButton.setIcon(R.drawable.alert_btn);
            dynamicButton.setActionView(new View(this)); // View를 추가하여 setTag를 사용할 수 있도록 함
            dynamicButton.getActionView().setTag("알림");
            }
        else if(toolbar_identifier.equals("등록")){
            dynamicButton = toolbar.getMenu().add(Menu.NONE, Menu.NONE, 1, "등록 버튼");
            dynamicButton.setIcon(R.drawable.ic_reg);
            dynamicButton.setActionView(new View(this)); // View를 추가하여 setTag를 사용할 수 있도록 함
            dynamicButton.getActionView().setTag("등록");
        }
        else if(toolbar_identifier.equals("하단 바 없애기")){
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.GONE); //하단바가 일시적으로 사라짐 (이후에 다른 프레그먼트로 넘어가면 다시 하단바가 생김)
        }



        // 버튼에 대한 클릭 이벤트 처리
        dynamicButton.setOnMenuItemClickListener(item -> {
            String tag = (String) item.getActionView().getTag();
            if (tag != null && tag.equals("알림")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentNotification).commit();
                return true;
            }
            else if(tag != null && tag.equals("등록")) {
                //등록 버튼은 누르면 게시글이 등록되어야 하기 때문에 따로 구현이 필요할듯

                showToast("등록이 완료되었습니다.");
                //등록 후에 토스트 띄우고 홈 화면으로 가도록
                getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
                return true;
            }
            else {
                return false;
            }
        });

        // 동적으로 추가한 버튼을 상단바에 보이도록 갱신
        toolbar.postInvalidate();

    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

