package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appbandochoi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThongBaoDatHangActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private Button btnReturnhome, btnReturnorder;
    private ImageView imgBack;
    private TextView textViewThongBao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_bao_dat_hang);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btnReturnhome.setOnClickListener(this);
        btnReturnorder.setOnClickListener(this);

        Intent intent = getIntent();
        int status = intent.getIntExtra("status", -1);
        if (status == 0)
            textViewThongBao.setText("Đặt hàng thành công! Quý khách hãy thanh toán ở trong mục đơn hàng!");
        if (status == 1)
            textViewThongBao.setText("Đặt hàng và thanh toán thành công. Xin cảm ơn quý khách!");
    }

    public void anhXa() {
        textViewThongBao = findViewById(R.id.textViewThongBao);
        linearTrangchu = findViewById(R.id.linearTrangchu);
        linearSanpham = findViewById(R.id.linearSanpham);
        linearDonhang = findViewById(R.id.linearDonhang);
        linearTaikhoan = findViewById(R.id.linearTaikhoan);
        btnCart = findViewById(R.id.btnCart);
        imgBack = findViewById(R.id.imgBack);
        btnReturnhome = findViewById(R.id.btnReturnHome);
        btnReturnorder = findViewById(R.id.btnGoToOrder);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(linearTrangchu)) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
        if (view.equals(linearSanpham)) {
            finish();
            startActivity(new Intent(this, DanhSachSPActivity.class));
        }
        if (view.equals(linearDonhang)) {
            finish();
            startActivity(new Intent(this, XemDonActivity.class));
        }
        if (view.equals(linearTaikhoan)) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        if (view.equals(btnCart)) {
            finish();
            startActivity(new Intent(this, GioHangActivity.class));
        }
        if (view.equals(imgBack)) {
            finish();
            startActivity(new Intent(this, GioHangActivity.class));
        }
        if (view.equals(btnReturnhome)) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
        if (view.equals(btnReturnorder)) {
            finish();
            startActivity(new Intent(this, XemDonActivity.class));
        }
    }
}