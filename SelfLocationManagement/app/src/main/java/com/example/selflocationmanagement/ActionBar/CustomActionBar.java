package com.example.selflocationmanagement.ActionBar;

import android.app.ActionBar;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.selflocationmanagement.R;

public class CustomActionBar {

    private Activity activity;                                                                      // 커스텀 액션바를 배치시킬 액티비티
    private ActionBar actionBar;                                                                    // 커스텀 액션바 변수

    public CustomActionBar(Activity activity, ActionBar actionbar){                                 // 본 클래스 생성 시, 초기화

        this.activity = activity;
        this.actionBar = actionbar;
    }

    public void setActionBar(){

        actionBar.setDisplayShowCustomEnabled(true);                                                // 커스텀 한 것을 표시
        actionBar.setDisplayHomeAsUpEnabled(false);                                                 // 액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);                                                // 액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);                                                 // 홈 아이콘을 숨김처리합니다.

        View mCustomView = LayoutInflater.from(activity)                                            // R.layout.custom_actionbar.xml을 mCustomView에 불러온다.
                .inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(mCustomView);                                                       // 커스텀 바에 위의 레이아웃 설정
    }
}
