package com.libyuv.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.libyuv.util.YuvUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YuvUtil.test();
    }
}
