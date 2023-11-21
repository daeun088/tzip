package com.example.tzip;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tzip.databinding.ActivityMainBinding;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // 뷰 바인딩

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SlidingUpPanelLayout slidePanel = binding.mainFrame; // SlidingUpPanel
//        slidePanel.addPanelSlideListener(new PanelEventListener()); // 이벤트 리스너 추가

        // 패널 열고 닫기
//        binding.btnToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SlidingUpPanelLayout.PanelState state = slidePanel.getPanelState();
//                // 닫힌 상태일 경우 열기
//                if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
//                }
//                // 열린 상태일 경우 닫기
//                else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                }
//            }
//        });

        // 터치로 슬라이드 가능 여부 설정 (panelState 변경으로 여닫는 건 가능)


        // 패널 활성화 여부 설정 (터치, 함수 모두 불가능)
//        binding.btnEnable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean enabled = slidePanel.isEnabled();
//                if (enabled) {
//                    binding.btnEnable.setText("활성화");
//                    slidePanel.setEnabled(false);
//                } else {
//                    binding.btnEnable.setText("비활성화");
//                    slidePanel.setEnabled(true);
//                }
//            }
//        });
//    }

    // 이벤트 리스너
//    private class PanelEventListener implements SlidingUpPanelLayout.PanelSlideListener {
//        // 패널이 슬라이드 중일 때
//        @Override
//        public void onPanelSlide(View panel, float slideOffset) {
//            binding.tvSlideOffset.setText(String.valueOf(slideOffset));
//        }
//
//        // 패널의 상태가 변했을 때
//        @Override
//        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                binding.btnToggle.setText("열기");
//            } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                binding.btnToggle.setText("닫기");
//            }
//        }
    }
}
