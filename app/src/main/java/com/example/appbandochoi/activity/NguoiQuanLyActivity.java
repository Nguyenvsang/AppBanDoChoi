package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NguoiQuanLyActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgDanhMuc, imgSanPham, imgDonHang, imgDoanhThu, imgDanhGia, imgKhachHang, imgBack;
    private TextView textViewHi;
    private User user;

    public void anhXa() {
        imgDanhMuc = findViewById(R.id.imageViewDanhMuc);
        imgSanPham = findViewById(R.id.imageViewSanPham);
        imgDonHang = findViewById(R.id.imageViewDonHang);
        imgDoanhThu = findViewById(R.id.imageViewDoanhThu);
        imgDanhGia = findViewById(R.id.imageViewDanhGia);
        imgKhachHang = findViewById(R.id.imageViewKhachHang);
        imgBack = findViewById(R.id.imgBack);
        textViewHi = findViewById(R.id.textViewHi);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nguoi_quan_ly);

        anhXa();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                user = SharedPrefManager.getInstance(this).getUser();
                textViewHi.setText("Hi " + user.getUsername());
            } catch (ParseException e) {
                startActivity(new Intent(NguoiQuanLyActivity.this, DangNhapActivity.class));
                finish();
            }

        } else {
            startActivity(new Intent(NguoiQuanLyActivity.this, DangNhapActivity.class));
            finish();
        }

        imgDanhMuc.setOnClickListener(NguoiQuanLyActivity.this);
        imgSanPham.setOnClickListener(NguoiQuanLyActivity.this);
        imgDonHang.setOnClickListener(NguoiQuanLyActivity.this);
        imgDoanhThu.setOnClickListener(NguoiQuanLyActivity.this);
        imgDanhGia.setOnClickListener(NguoiQuanLyActivity.this);
        imgKhachHang.setOnClickListener(NguoiQuanLyActivity.this);
        imgBack.setOnClickListener(NguoiQuanLyActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (!user.isRole()) {
            if (v.equals(imgDanhMuc)) {

            }
            if (v.equals(imgSanPham)) {
                finish();
                startActivity(new Intent(this, QuanLySanPhamActivity.class));
            }
            if (v.equals(imgDonHang)) {
                finish();
                startActivity(new Intent(this, QuanLyDonHangActivity.class));
            }
            if (v.equals(imgDoanhThu)) {
                finish();
                startActivity(new Intent(this, QuanLyDoanhThuActivity.class));
            }
            if (v.equals(imgDanhGia)) {
                finish();
                startActivity(new Intent(this, QuanLyDanhGiaActivity.class));
            }
            if (v.equals(imgKhachHang)) {
            }
            if (v.equals(imgBack)) {
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
            }
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập vào tài khoản quản lý!", Toast.LENGTH_SHORT).show();
        }
    }
}