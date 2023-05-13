package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbandochoi.R;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {
    APIService apiService;
    EditText etFirstname, etLastname, etUserName, etPassword, etEmail, etRepeatPassword, etPhone;
    //TextView textViewLogin;
    Button btnSignup;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        anhXa();
        // Gọi userSignup()
        btnSignup.setOnClickListener(this);
        imgBack.setOnClickListener(this);

    }

    public void anhXa() {
        etFirstname = findViewById(R.id.editTextFirstname);
        etLastname = findViewById(R.id.editTextLastname);
        etUserName = findViewById(R.id.editTextUsername);
        etPassword = findViewById(R.id.editTextPassword);
        etRepeatPassword = findViewById(R.id.editTextRepeatPassword);
        etEmail = findViewById(R.id.editTextEmail);
        etPhone = findViewById(R.id.editTextPhone);
        btnSignup = findViewById(R.id.btndangky);
        imgBack = findViewById(R.id.imgBack);
    }

    public void userSignup() {
        // Lấy giá trị
        final String firstname = etFirstname.getText().toString().trim();
        final String lastname = etLastname.getText().toString().trim();
        final String username = etUserName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String repeatedPassword = etRepeatPassword.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        // Kiểm tra đầu vào
        if (TextUtils.isEmpty(firstname)) {
            etFirstname.setError("Vui lòng nhập tên!");
            etFirstname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email!");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etUserName.setError("Vui lòng nhập tên người dùng!");
            etUserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu!");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 8) {
            etPassword.setError("Mật khẩu phải có trên 8 ký tự!");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(repeatedPassword)) {
            etRepeatPassword.setError("Mật khẩu không trùng nhau!");
            etRepeatPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }
        // Gọi Interface APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.signup(username, password, firstname, lastname, email, phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject createdUser;
                try {
                    String responseBody = response.body().string();
                    createdUser = new JSONObject(responseBody);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (createdUser.getString("message").equals("Username has been already taken.")) {
                        Toast.makeText(DangKyActivity.this, "Tên người dùng đã được đăng ký!", Toast.LENGTH_SHORT).show();
                    } else if (createdUser.getString("message").equals("Email has been already registered.")) {
                        Toast.makeText(DangKyActivity.this, "Email đã được đăng ký!", Toast.LENGTH_SHORT).show();
                    } else if (createdUser.getString("message").equals("Sign up successfully.")) {
                        createdUser = createdUser.getJSONObject("data");
                        // Tạo người dùng
                        DateUtil du = new DateUtil();
                        Timestamp birthDay = null, createdAt = null, updatedAt = null;
                        if (!createdUser.isNull("birthDay"))
                            birthDay = du.parseDate(createdUser.getString("birthDay"));
                        if (!createdUser.isNull("createdAt"))
                            createdAt = du.parseDate(createdUser.getString("createdAt"));
                        if (!createdUser.isNull("updatedAt"))
                            updatedAt = du.parseDate(createdUser.getString("updatedAt"));

                        User user = new User(createdUser.getInt("userID"),
                                createdUser.getString("firstname"),
                                createdUser.getString("lastname"),
                                createdUser.getInt("gender"),
                                birthDay,
                                createdUser.getString("email"),
                                createdUser.getString("address"),
                                createdUser.getString("phone"),
                                createdUser.getString("image"),
                                createdUser.getBoolean("role"),
                                createdUser.getBoolean("status"),
                                createdUser.getString("username"),
                                createdAt,
                                updatedAt);
                        // Bắt đầu MainActivity
                        Toast.makeText(DangKyActivity.this, "Đăng ký thành công. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(DangKyActivity.this, DangNhapActivity.class));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(DangKyActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSignup)) {
            userSignup();
        }
        if (v.equals(imgBack)) {
            finish();
            startActivity(new Intent(this, DangNhapActivity.class));
        }
    }
}