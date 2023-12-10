package com.example.tzip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.view.View;

import android.widget.Toast;

public class nevigation_bar_test_code extends AppCompatActivity {

    TabLayout tabs;

    ImageView logoImageView;

    RelativeLayout schedule_block;

    private Toolbar toolbar;
    Fragment_community fragmentCommunity;
    Fragment_schedule fragmentSchedule;
    Fragment_record fragmentRecord;
    Fragment_home fragmentHome;
    Fragment_mypage fragmentMypage;
    Fragment_notification fragmentNotification;

    Fragment_schedule_plan fragmentSchedulePlan;

    Fragment_schedule_plan_write fragmentSchedulePlanWrite;

    FriendTripRecord friendTripRecord;

    MyTripRecord myTripRecord;

    RecordAdd recordAdd;

    RecordRead recordRead;

    RecordWriting recordWriting;

    Fragment_friend_request fragmentFriendRequest;

    FriendList friendList;

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
        fragmentSchedulePlan = new Fragment_schedule_plan();
        fragmentSchedulePlanWrite = new Fragment_schedule_plan_write();
        friendTripRecord = new FriendTripRecord();
        myTripRecord = new MyTripRecord();
        recordAdd = new RecordAdd();
        recordRead = new RecordRead();
        recordWriting = new RecordWriting();
        fragmentFriendRequest = new Fragment_friend_request();
        friendList = new FriendList();

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
        tabs = findViewById(R.id.tab_layout);

        changeTabText(0,"1" );


        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0)
                    selected = fragmentSchedulePlan;
                else if(position == 1)
                    selected = fragmentSchedulePlan;
                else if(position == 2)
                    selected = fragmentSchedulePlan; //여행 기간이 설정되면 그 일수만큼 position과 fragment가 추가되도록 하면 될듯
                else
                    selected = fragmentSchedulePlan;
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
    protected void changeTabText(int tabIndex, String newText) {
        TabLayout.Tab tab = tabs.getTabAt(tabIndex);
        if (tab != null) {
            tab.setText(newText);
        }
    }

    // 홈 프래그먼트에 대한 상단바 설정
    protected void setToolbarForHome() {
        setToolbarContent("", false);
        addButtonToToolbar("알림");
        addButtonToToolbar("로고");
    }

    // 커뮤니티 프래그먼트에 대한 상단바 설정
    protected void setToolbarForCommunity() {
        setToolbarContent("여행 일정 공유", false);
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
    protected void setToolbarForMyTripRecord() {
        setToolbarContent("나의 여행 기록", true);
        addButtonToToolbar("알림");
    }
    protected void setToolbarForFriendTripRecord() {
        setToolbarContent("친구의 여행 기록", true);
        addButtonToToolbar("알림");
    }
    protected void setToolbarForEmergencyMessage() {
        setToolbarContent("긴급메시지", true);
        addButtonToToolbar("버튼 없애기");
        addButtonToToolbar("하단 바 없애기");
    }
    protected void setToolbarForRecordWriting(){
        setToolbarContent("글쓰기", true);
        addButtonToToolbar("등록");
        addButtonToToolbar("하단 바 없애기");
    }
    protected void setToolbarForFriendList(){
        setToolbarContent("친구 목록", true);
        addButtonToToolbar("알림");
    }
    protected void setToolbarForFriendRequest(){
        setToolbarContent("친구 추가", true);
        addButtonToToolbar("알림");
    }
    protected void setToolbarForSchedulePlan(){
        setToolbarContent("일정", true);
        addButtonToToolbar("알림");
        addButtonToToolbar("탭 추가");
    }
    protected void setToolbarForSchedulePlanWrite(){
        setToolbarContent("상세 일정", true);
        addButtonToToolbar("알림");
        addButtonToToolbar("하단 바 없애기");
    }
    protected void setToolbarForCommunityAdd(){
        setToolbarContent("일정 참여자 구하기",true);
        addButtonToToolbar("알림");
        addButtonToToolbar("하단 바 없애기");
    }
    protected void setTollbarForCommunityStory(){
        setToolbarContent("여행 일정 공유하기",true);
        addButtonToToolbar("알림");
    }

    @Override
    public void onBackPressed() {//밑줄 상관 x
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESS_INTERVAL) {
            // 두 번째로 뒤로가기 키를 눌렀을 때
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            System.exit(0);
            super.onBackPressed();
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

    protected void handleSomeMenuItemClick() {
        // 원하는 동작 수행
        // 예: 네비게이션 바 변경
        NevigationBarChange(post_id);
    }

    protected void NevigationBarChange(int post_id_) {
        // 네비게이션 바 변경에 대한 로직 작성
        if (post_id_ == R.id.Home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);//하단바의 visibility 바꿔주기
            setToolbarForHome();
        } else if (post_id_ == R.id.community) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentCommunity).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForCommunity();
        } else if (post_id_ == R.id.review) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentRecord).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForRecord();
        } else if (post_id_ == R.id.schedule) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedule).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForSchedule();
        } else if (post_id_ == R.id.mypage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
            BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
            navigationBarView.setVisibility(View.VISIBLE);
            setToolbarForMypage();
        }else if (post_id_ == R.id.Schedule_plan) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedulePlan).commit();
            setToolbarForSchedulePlan();
            post_id = R.id.schedule;
        } else if (post_id_ == R.id.Schedule_plan_write) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSchedulePlanWrite).commit();
            setToolbarForSchedulePlanWrite();
            post_id = R.id.Schedule_plan;
        } else if (post_id_ == R.id.Record_add) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, recordAdd).commit();
            setToolbarForAddRecord();
            post_id = R.id.review;
        } else if (post_id_ == R.id.My_trip_record) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, myTripRecord).commit();
            setToolbarForMyTripRecord();
            post_id = R.id.review;
        } else if (post_id_ == R.id.Friend_trip_record) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, friendTripRecord).commit();
            setToolbarForFriendTripRecord();
            post_id = R.id.review;
        } else if (post_id_ == R.id.Friend_request) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentFriendRequest).commit();
            setToolbarForFriendRequest();
        } else if (post_id_ == R.id.Friend_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, friendList).commit();
            setToolbarForFriendList();
            post_id = R.id.mypage;
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
            ImageView existingLogoImageView = findViewById(R.id.logoImageVIew);
            TabLayout tabLayout = findViewById(R.id.tab_layout);
            if (existingLogoImageView != null) {
                toolbar.removeView(existingLogoImageView); // 기존 ImageView 삭제
            }
            if (toolbar_identifier.equals("알림")) { //상단바에 알림 버튼이 필요한 경우
                toolbar_button.setImageResource(R.drawable.ic_alert);
                toolbar_button.setTag("알림");
                toolbar_button.setVisibility(View.VISIBLE); //visibility visible로 바꿔주기
                tabLayout.setVisibility(View.GONE);
            } else if (toolbar_identifier.equals("등록")) {
                toolbar_button.setImageResource(R.drawable.ic_reg);
                toolbar_button.setTag("등록");
                toolbar_button.setVisibility(View.VISIBLE);
            } else if (toolbar_identifier.equals("하단 바 없애기")) {
                BottomNavigationView navigationBarView = findViewById(R.id.bottom_navigationview);
                navigationBarView.setVisibility(View.GONE);
            } else if (toolbar_identifier.equals("버튼 없애기")) {
                toolbar_button.setVisibility(View.GONE);
            } else if (toolbar_identifier.equals("로고")) {
                toolbar_button.setVisibility(View.VISIBLE);
                logoImageView = new ImageView(this);
                logoImageView.setId(R.id.logoImageVIew);
                logoImageView.setImageResource(R.drawable.logo);
                Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.START;
                toolbar.addView(logoImageView, layoutParams);
            } else if (toolbar_identifier.equals("탭 추가")) {
                tabLayout.setVisibility(View.VISIBLE);
            }

            toolbar_button.setOnClickListener(view -> {
                String tag = (String) toolbar_button.getTag();
                if (tag != null &&tag.equals("알림")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentNotification).addToBackStack(null).commit();
                    setToolbarForNotification();
                } else if (tag != null && tag.equals("등록")) {
                        if(RecordWriting.saveTitle( this)==false){
                            showToast("제목을 입력하세요.");
                        } else if( RecordWriting.saveImage(this)==false){
                            showToast("이미지를 설정해주세요.");
                        }
                        else if(RecordWriting.contentBlock(this) ==false){
                            showToast("일정을 추가해주세요.");
                        }
                        else if(RecordWriting.saveTitle(this)){
                            showToast("등록이 완료되었습니다.");
                            //등록 후에 토스트 띄우고 홈 화면으로 가도록
                            post_id = R.id.review;
                            NevigationBarChange(post_id);
                        }
                }
            });
        }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


//post_id를 통해서 이전 프레그먼트의 id를 기억해놓고 뒤로가기 버튼 클릭 시 onOptionsItemSelected()에서 기억해놓은 id로 해당 프레그먼트 실행
//하단바와 상단바 버튼의 visivility는 계속 재설정 해줘야함. 하단바 : 알림 창에 들어간 후 뒤로가기 버튼 클릭시 사라졌던 하단바가 돌아오지 않음