package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.appbandochoi.R;

public class ThongBaoDatHangActivity extends AppCompatActivity {

    private TextView textViewThongBao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_bao_dat_hang);

        textViewThongBao = findViewById(R.id.textViewThongBao);

        Intent intent = getIntent();
        int status = intent.getIntExtra("status", -1);
        if (status == 0)
            textViewThongBao.setText("Đặt hàng thành công! Quý khách hãy thanh toán ở trong mục đơn hàng!");
        if (status == 1)
            textViewThongBao.setText("Đặt hàng và thanh toán thành công. Xin cảm ơn quý khách!");
    }
}