package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.OrderItemAdapter;
import com.example.appbandochoi.adapter.ReviewAdapter;
import com.example.appbandochoi.databinding.ActivityChiTietBinding;
import com.example.appbandochoi.databinding.ActivityChiTietDonHangBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;

import java.text.ParseException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietDonHangActivity extends AppCompatActivity implements OrderItemAdapter.OnItemClickListener, View.OnClickListener {

    private APIService apiService;
    private ActivityChiTietDonHangBinding binding;
    private Order order;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        order = (Order) bundle.getSerializable("order");

        orderItemAdapter = new OrderItemAdapter(orderItemList);

        System.out.println(order.getOrderID());
        getOrderItemList(order.getOrderID());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chi_tiet_don_hang);
        binding.setOrderitem(this);
    }

    public void getOrderItemList(int orderID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderItemByOrder(orderID).enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(Call<List<OrderItem>> call, Response<List<OrderItem>> response) {
                if (response.isSuccessful()) {
                    orderItemList = response.body();
                    System.out.println(orderItemList);
                    orderItemAdapter = new OrderItemAdapter(orderItemList);
                    binding.recycleviewMathang.setLayoutManager(new LinearLayoutManager(ChiTietDonHangActivity.this));
                    binding.recycleviewMathang.setAdapter(orderItemAdapter);
                    orderItemAdapter.notifyDataSetChanged();
                    orderItemAdapter.setOnItemClickListener((OrderItemAdapter.OnItemClickListener) ChiTietDonHangActivity.this);
                } else
                    Toast.makeText(ChiTietDonHangActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<OrderItem>> call, Throwable t) {
                Toast.makeText(ChiTietDonHangActivity.this, "Gọi API OI thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(OrderItem orderItem) {
    }

    @Override
    public void onClick(View view) {

    }
}