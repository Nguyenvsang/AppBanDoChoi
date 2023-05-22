package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.appbandochoi.MainActivity;
import com.example.appbandochoi.R;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;

public class IntroActivity extends AppCompatActivity {

    private LottieAnimationView animation_view;
    private Button btnIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        animation_view = findViewById(R.id.animation_view);
        btnIntro = findViewById(R.id.btnIntro);

        animation_view.setVisibility(View.GONE);
        animation_view.cancelAnimation();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(IntroActivity.this, HomeActivity.class));
            finish();
        }

        // Tạp thread để chạy animation loading
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception ex) {

                } finally {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation_view.setVisibility(View.VISIBLE);
                animation_view.playAnimation();
                thread.start();
            }
        });
    }
}