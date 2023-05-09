package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appbandochoi.R;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvGender, tvBirthday, tvAddress, tvEmail, tvUsername, tvPhone, tvRole;
    ImageView imgProfile;

    FloatingActionButton btnCart;
    public void anhXa() {
        tvName = findViewById(R.id.textViewName);
        tvGender = findViewById(R.id.textViewGenderProfile);
        tvBirthday = findViewById(R.id.textViewBirthdayProfile);
        tvAddress = findViewById(R.id.textViewAdressProfile);
        tvEmail = findViewById(R.id.textViewEmailProfile);
        tvUsername = findViewById(R.id.textViewUsernameProfile);
        tvRole = findViewById(R.id.textViewRoleProfile);
        tvPhone = findViewById(R.id.textViewPhoneProfile);
        imgProfile = findViewById(R.id.imageViewProfile);
        btnCart = findViewById(R.id.btnCart);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        anhXa();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = null;
            try {
                user = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                startActivity(new Intent(ProfileActivity.this, DangNhapActivity.class));
                finish();
            }
            System.out.println(user);
            tvUsername.setText(user.getUsername());
            tvName.setText(user.getLastname().concat((" ").concat(user.getFirstname())));
            if (user.getGender() == 0)
                tvGender.setText("Nam");
            else if (user.getGender() == 1)
                tvGender.setText("Nữ");
            else
                tvGender.setText("Khác");
            if (user.getBirthDay() != null)
                tvBirthday.setText(user.getBirthDay().toString());
            else
                tvBirthday.setText("");
            tvAddress.setText(user.getAddress());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone());
            tvRole.setText(user.isRole() == false ? "Quản lý" : "Khách hàng");
            Glide.with(getApplicationContext()).load(user.getImage().equals("") ? R.drawable.profile : user.getImage()).into(imgProfile);
            //bắt sự kiện nhấn nút đăng xuất
            //btnLogout.setOnClickListener(ProfileActivity.this);
        } else {
            startActivity(new Intent(ProfileActivity.this, DangNhapActivity.class));
            finish();
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, GioHangActivity.class));
            }
        });
    }

//    public void onClick(View view) {
//        if (view.equals(btnLogout)) {
//            SharedPrefManager.getInstance(getApplicationContext()).logOut();
//            finish();
//            startActivity(new Intent(this, LoginActivity.class));
//        } else if (view.equals(btnHome)) {
//            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//                finish();
//                startActivity(new Intent(this, HomeActivity.class));
//            }
//        }
//        else if (view.equals(eAvatar)) {
//            finish();
//            startActivity(new Intent(this, UpdateImageActivity.class));
//        }
//    }
}