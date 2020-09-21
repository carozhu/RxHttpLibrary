package com.carozhu.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.carozhu.rxhttp.ApiHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ApiHelper.getInstance().getAPIService()
    }
}
