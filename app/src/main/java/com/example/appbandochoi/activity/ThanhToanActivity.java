package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private User user;
    private ActivityThanhToanBinding binding;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get userID
        User thisUser = new User();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                thisUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        user = getUser(thisUser.getUserID());
        //System.out.println(user.getFirstname());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_thanh_toan);
        binding.setUser(user);

        // Set total price
        Intent intent = getIntent();
        long totalOrder = intent.getIntExtra("totalOrder", 0);
        binding.txttongtien.setText(String.valueOf(totalOrder).concat(" VNĐ"));
        // Click event
        binding.btndathang.setOnClickListener(ThanhToanActivity.this);
    }

    public User getUser(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUser(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    System.out.println(user.getUserID());
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
        return user;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(binding.btndathang)) {
            placeOrder(user.getUserID(), 1);
        }
    }

    public void placeOrder(int userID, int status) {
        Map<String, Object> orderModel = new HashMap<>();
        orderModel.put("status", status);
        orderModel.put("receiverName", binding.edtten.getText().toString());
        orderModel.put("phone", binding.edtsodienthoai.getText().toString());
        orderModel.put("address", binding.edtdiachi.getText().toString());
        Gson gson = new Gson();
        String json = gson.toJson(gson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.placeOrder(userID, requestBody).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(ThanhToanActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ThanhToanActivity.this, HomeActivity.class));
                } else
                    Toast.makeText(ThanhToanActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("Error", t.toString());
                Toast.makeText(ThanhToanActivity.this, "Gọi API lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
