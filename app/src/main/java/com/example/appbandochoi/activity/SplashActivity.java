package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.appbandochoi.MainActivity;
import com.example.appbandochoi.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(1500);
                }catch (Exception ex){

                }finally {
                    //Chua co hoat dong dang nhap nen tam bo qua
                    //Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                    //Cho thu chuyen sang trang chinh de thu nghiem
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}