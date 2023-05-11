package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appbandochoi.R;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.ShortDateUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvGender, tvBirthday, tvAddress, tvEmail, tvUsername, tvPhone, tvRole;
    ImageView imgProfile;
    FloatingActionButton btnCart;
    private User user;
    private APIService apiService;
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

        // Check to get from update
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle == null) {
            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                User storedUser = null;
                try {
                    storedUser = SharedPrefManager.getInstance(this).getUser();
                } catch (ParseException e) {
                    startActivity(new Intent(ProfileActivity.this, DangNhapActivity.class));
                    finish();
                }

                user = getUser(storedUser.getUserID());

                //bắt sự kiện nhấn nút đăng xuất
                //btnLogout.setOnClickListener(ProfileActivity.this);
            } else {
                startActivity(new Intent(ProfileActivity.this, DangNhapActivity.class));
                finish();
            }
        } else {
            user = (User) bundle.getSerializable("user");
            displayUser();
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, GioHangActivity.class));
            }
        });
    }

    public User getUser(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUser(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    displayUser();

                } else {
                    int statusCode = response.code();
                    Toast.makeText(ProfileActivity.this, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(ProfileActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        return user;
    }

    public void displayUser() {
        // Set text
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getLastname().concat((" ").concat(user.getFirstname())));
        if (user.getGender() == 0)
            tvGender.setText("Nam");
        else if (user.getGender() == 1)
            tvGender.setText("Nữ");
        else
            tvGender.setText("Khác");
        if (user.getBirthDay() != null)
            tvBirthday.setText(new ShortDateUtil().parseShortDate(user.getBirthDay()));
        else
            tvBirthday.setText("");
        tvAddress.setText(user.getAddress());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());
        tvRole.setText(user.isRole() == false ? "Quản lý" : "Khách hàng");
        Glide.with(getApplicationContext()).load(user.getImage().equals("") ? R.drawable.profile : Constants.ROOT_URL.concat(user.getImage())).into(imgProfile);
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