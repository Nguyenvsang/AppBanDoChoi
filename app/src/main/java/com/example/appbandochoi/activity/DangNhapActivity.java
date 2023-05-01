package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbandochoi.MainActivity;
import com.example.appbandochoi.R;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangNhapActivity extends AppCompatActivity {
    APIService apiService;
    EditText eTUsername, eTPassword;
    Button btnLogin;
    TextView tVSignup;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        anhXa();

        // Login
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void anhXa() {
        eTUsername = (EditText) findViewById(R.id.editTextUsername);
        eTPassword = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.btnDangNhap);
        tVSignup = (TextView) findViewById(R.id.txtDangKy);
    }

    public void login() {
        // Lấy giá trị
        final String username = eTUsername.getText().toString().trim();
        final String password = eTPassword.getText().toString().trim();
        // Kiểm tra đầu vào
        if (TextUtils.isEmpty(username)) {
            eTUsername.setError("Vui lòng nhập tên người dùng!");
            eTUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            eTPassword.setError("Vui lòng nhập mật khẩu!");
            eTPassword.requestFocus();
            return;
        }
        // Gọi Interface APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.login(username, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject responseMessage;
                try {
                    String responseBody;
                    if(response.body() == null)
                        responseBody = response.errorBody().string();
                    else
                        responseBody = response.body().string();
                    responseMessage = new JSONObject(responseBody);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if(responseMessage.getString("message").equals("Invalid username or password.")) {
                        Toast.makeText(DangNhapActivity.this, "Sai tên người dùng hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lấy dữ liệu người dùng
                        JSONObject receivedUser = responseMessage.getJSONObject("data");
                        // Tạo người dùng
                        DateUtil du = new DateUtil();
                        Timestamp birthDay = null, createdAt = null, updatedAt = null;
                        if (!receivedUser.isNull("birthDay"))
                            birthDay = du.parseDate(receivedUser.getString("birthDay"));
                        if (!receivedUser.isNull("createdAt"))
                            createdAt = du.parseDate(receivedUser.getString("createdAt"));
                        if (!receivedUser.isNull("updatedAt"))
                            updatedAt = du.parseDate(receivedUser.getString("updatedAt"));

                        User user = new User(receivedUser.getInt("userID"),
                                receivedUser.getString("firstname"),
                                receivedUser.getString("lastname"),
                                receivedUser.getInt("gender"),
                                birthDay,
                                receivedUser.getString("email"),
                                receivedUser.getString("address"),
                                receivedUser.getString("phone"),
                                receivedUser.getString("image"),
                                receivedUser.getBoolean("role"),
                                receivedUser.getBoolean("status"),
                                receivedUser.getString("username"),
                                createdAt,
                                updatedAt);
                        // Lưu người dùng
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(DangNhapActivity.this, MainActivity.class));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (ParseException p) {
                    throw new RuntimeException(p);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error", t.toString());
                Toast.makeText(DangNhapActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}