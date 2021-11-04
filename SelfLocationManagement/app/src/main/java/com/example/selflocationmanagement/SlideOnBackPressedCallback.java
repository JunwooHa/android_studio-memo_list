package com.example.selflocationmanagement;

import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

class SlideOnBackPressedCallback extends OnBackPressedCallback
        implements SlidingPaneLayout.PanelSlideListener {

    private final SlidingPaneLayout mSlidingPaneLayout;

    SlideOnBackPressedCallback(@NonNull SlidingPaneLayout slidingPaneLayout) {
        // Set the default 'enabled' state to true only if it is slideable (i.e., the panes
        // are overlapping) and open (i.e., the detail pane is visible).
        super(slidingPaneLayout.isSlideable() && slidingPaneLayout.isOpen());
        mSlidingPaneLayout = slidingPaneLayout;
        mSlidingPaneLayout.setPanelSlideListener(this);
    }

    @Override
    public void handleOnBackPressed() {
        // Return to the list pane when the system back button is pressed.
        mSlidingPaneLayout.closePane();
    }

    @Override
    public void onPanelSlide(@NonNull View panel, float slideOffset) { }

    @Override
    public void onPanelOpened(@NonNull View panel) {
        // Intercept the system back button when the detail pane becomes visible.
        setEnabled(true);
    }

    @Override
    public void onPanelClosed(@NonNull View panel) {
        // Disable intercepting the system back button when the user returns to the
        // list pane.
        setEnabled(false);
    }
}
