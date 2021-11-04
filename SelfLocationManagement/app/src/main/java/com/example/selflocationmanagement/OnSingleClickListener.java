package com.example.selflocationmanagement;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;
    public abstract  void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsesdTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        if(elapsesdTime<=MIN_CLICK_INTERVAL)
            return;

        onSingleClick(v);

    }
}