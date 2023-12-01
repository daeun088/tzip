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
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;

import org.w3c.dom.Text;

import android.app.Activity;

public class nevigation_bar_test_code extends AppCompatActivity {


    private Toolbar toolbar;
    Fragment_community fragmentCommunity;
    Fragment_schedule fragmentSchedule;
    Fragment_record fragmentRecord;
    Fragment_home fragmentHome;
    Fragment_mypage fragmentMypage;
    Fragment_notification fragmentNotification;

    TextView toolbar_title;
    ImageButton toolbar_button;

    private long backPressedTime = 0;
    private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds

    int post_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation_bar_test_code);

        fragmentSchedule = new Fragment_schedule();
        fragmentRecord = new Fragment_record();
        fragmentCommunity = new Fragment_community();
        fragmentHome = new Fragment_home();
        fragmentMypage = new Fragment_mypage();
        fragmentNotification = new Fragment_notification();


        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_button = findViewById(R.id.toolbar_Button);

        BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setSelectedItemId(R.id.Home);

        // 프래그먼트 초기화 및 화면 설정
        initFragments();


        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                post_id = item.getItemId();
                if (post_id == R.id.Home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
                    setToolbarForHome();
                    return true;
                } else if (post_id == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentCommunity).commit();
                    setToolbarForCommunity();
                    return true;
                } else if (post_id == R.id.review) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentRecord).commit();
                    setToolbarForRecord();
                    return true;
                } else if (post_id == R.id.schedule) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedule).commit();
                    setToolbarForSchedule();
                    return true;
                } else if (post_id == R.id.mypage) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
                    setToolbarForMypage();
                    return true;
                }
                return false;
            }
        });
    }

    // 홈 프래그먼트에 대한 상단바 설정
    protected void setToolbarForHome() {
        setToolbarContent("홈", false);
        addButtonToToolbar("알림");
    }

    // 커뮤니티 프래그먼트에 대한 상단바 설정
    protected void setToolbarForCommunity() {
        setToolbarContent("커뮤", false);
        addButtonToToolbar("알림");
    }

    // 레코드 프래그먼트에 대한 상단바 설정
    protected void setToolbarForRecord() {
        setToolbarContent("여행 기록", false);
        addButtonToToolbar("알림");
    }

    // 스케줄 프래그먼트에 대한 상단바 설정
    protected void setToolbarForSchedule() {
        setToolbarContent("나의 여행 일정", false);
        addButtonToToolbar("알림");
    }

    // 마이페이지 프래그먼트에 대한 상단바 설정
    protected void setToolbarForMypage() {
        setToolbarContent("마이페이지", false);
        addButtonToToolbar("알림");
    }
    // 알림 프래그먼트에 대한 상단바 설정
    protected void setToolbarForNotification() {
        setToolbarContent("알림", true);
        addButtonToToolbar("버튼 없애기");
        addButtonToToolbar("하단 바 없애기");
    }
    protected void setToolbarForAddRecord() {
        setToolbarContent("나의 여행 기록", true);
        addButtonToToolbar("하단 바 없애기");
        addButtonToToolbar("알림");
    }


    @Override
    public void onBackPressed() {//밑줄 상관 x
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESS_INTERVAL) {
            // 두 번째로 뒤로가기 키를 눌렀을 때
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            System.exit(0);
        } else {
            // 첫 번째로 뒤로가기 키를 눌렀을 때
            showToast("한 번 더 누르면 종료됩니다.");
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//상단바의 뒤로가기 버튼 클릭시의 이벤트 처리
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                handleSomeMenuItemClick();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void handleSomeMenuItemClick() {
        // 원하는 동작 수행
        // 예: 네비게이션 바 변경
        NevigationBarChange(post_id);
    }

    protected void NevigationBarChange(int post_id) {
        // 네비게이션 바 변경에 대한 로직 작성
        if (post_id == R.id.Home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);//하단바의 visibility 바꿔주기
            setToolbarForHome();
        } else if (post_id == R.id.community) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentCommunity).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForCommunity();
        } else if (post_id == R.id.review) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentRecord).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForRecord();
        } else if (post_id == R.id.schedule) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedule).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForSchedule();
        } else if (post_id == R.id.mypage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForMypage();
        }
    }


        // 프래그먼트 초기화
        private void initFragments () {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
            post_id = R.id.Home;
            setToolbarForHome();
    }


        protected void setToolbarContent (String title,boolean showBackButton){
            getSupportActionBar().setTitle(""); // 초기화

            toolbar_title.setText(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        }

        // 툴바에 버튼 추가
        protected void addButtonToToolbar (String toolbar_identifier){
            if (toolbar_identifier.equals("알림")) { //상단바에 알림 버튼이 필요한 경우
                toolbar_button.setImageResource(R.drawable.ic_alert);
                toolbar_button.setTag("알림");
                toolbar_button.setVisibility(View.VISIBLE); //visibility visible로 바꿔주기
            } else if (toolbar_identifier.equals("등록")) {
                toolbar_button.setImageResource(R.drawable.ic_reg);
                toolbar_button.setTag("등록");
                toolbar_button.setVisibility(View.VISIBLE);
            } else if (toolbar_identifier.equals("하단 바 없애기")) {
                BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
                navigationBarView.setVisibility(View.GONE);
            } else if (toolbar_identifier.equals("버튼 없애기")) {
                toolbar_button.setVisibility(View.GONE);
            }

            toolbar_button.setOnClickListener(view -> {
                //String tag = (String) toolbar.getTag();
                String tag = (String) toolbar_button.getTag();
                if (tag != null &&tag.equals("알림")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentNotification).addToBackStack(null).commit();
                    setToolbarForNotification();
                } else if (tag != null && tag.equals("등록")) {
                    //등록 버튼은 누르면 게시글이 등록되어야 하기 때문에 따로 구현이 필요할듯

                    showToast("등록이 완료되었습니다.");
                    //등록 후에 토스트 띄우고 홈 화면으로 가도록
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
                    setToolbarForHome();
                }
            });
        }



    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


//post_id를 통해서 이전 프레그먼트의 id를 기억해놓고 뒤로가기 버튼 클릭 시 onOptionsItemSelected()에서 기억해놓은 id로 해당 프레그먼트 실행
//하단바와 상단바 버튼의 visivility는 계속 재설정 해줘야함. 하단바 : 알림 창에 들어간 후 뒤로가기 버튼 클릭시 사라졌던 하단바가 돌아오지 않음