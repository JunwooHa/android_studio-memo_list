package com.example.selflocationmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(4000);
        }catch (InterruptedException e) { e.printStackTrace(); }

        startActivity(new Intent(this, MainActivity.class));                           // Intent를 이용한 Activity 이동  ( 현재 액티비티, 이동할 액티비티 )
        finish();
    }
}
