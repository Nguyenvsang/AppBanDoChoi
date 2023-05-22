package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CartItemAdapter;
import com.example.appbandochoi.databinding.ActivityThanhToanBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener {
    private User user, thisUser;
    private ActivityThanhToanBinding binding;
    private APIService apiService;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                thisUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            startActivity(new Intent(ThanhToanActivity.this, DangNhapActivity.class));
            finish();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_thanh_toan);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        getUser(thisUser.getUserID());

        // Set total price
        Intent intent = getIntent();
        long totalOrder = intent.getIntExtra("totalOrder", 0);
        binding.txttongtien.setText(String.valueOf(totalOrder).concat(" VNĐ"));
        // Click event
        binding.btndathang.setOnClickListener(ThanhToanActivity.this);
        binding.btnthanhtoansau.setOnClickListener(ThanhToanActivity.this);
    }

    public void anhXa() {
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
        imgBack = binding.imgBack;
    }

    public void getUser(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUser(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    binding.setUser(user);
                } else {
                    int statusCode = response.code();
                    Toast.makeText(ThanhToanActivity.this, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(ThanhToanActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.btndathang)) {
            placeOrder(thisUser.getUserID(), 1);
        }
        if (view.equals(binding.btnthanhtoansau)) {
            placeOrder(thisUser.getUserID(), 0);
        }
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
    }

    public void placeOrder(int userID, int status) {
        String receiverName = binding.edtten.getText().toString();
        String phone = binding.edtsodienthoai.getText().toString();
        String address = binding.edtdiachi.getText().toString();

        Map<String, Object> orderModel = new HashMap<>();
        orderModel.put("userID", userID);
        orderModel.put("status", status);
        orderModel.put("receiverName", receiverName);
        orderModel.put("phone", phone);
        orderModel.put("address", address);

        Gson gson = new Gson();
        String json = gson.toJson(orderModel);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.placeOrder(userID, requestBody).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ThanhToanActivity.this, ThongBaoDatHangActivity.class);
                    intent.putExtra("status", status);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(ThanhToanActivity.this, "Lỗi " + response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("Error", t.toString());
                Toast.makeText(ThanhToanActivity.this, "Gọi API lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
