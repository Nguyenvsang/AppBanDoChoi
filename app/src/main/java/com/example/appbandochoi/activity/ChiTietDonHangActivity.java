package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.OrderItemAdapter;
import com.example.appbandochoi.adapter.ReviewAdapter;
import com.example.appbandochoi.databinding.ActivityChiTietBinding;
import com.example.appbandochoi.databinding.ActivityChiTietDonHangBinding;
import com.example.appbandochoi.model.FullOrderItem;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private List<FullOrderItem> orderItemList;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        order = (Order) bundle.getSerializable("order");

        orderItemAdapter = new OrderItemAdapter(orderItemList, context);

        getOrderItemList(order.getOrderID());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chi_tiet_don_hang);
        binding.setOrderitem(this);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void anhXa() {
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
        imgBack = binding.imgBack;
    }

    public void getOrderItemList(int orderID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getFullOrderItemByOrder(orderID).enqueue(new Callback<List<FullOrderItem>>() {
            @Override
            public void onResponse(Call<List<FullOrderItem>> call, Response<List<FullOrderItem>> response) {
                if (response.isSuccessful()) {
                    orderItemList = response.body();
                    orderItemAdapter = new OrderItemAdapter(orderItemList, context);
                    binding.recycleviewMathang.setLayoutManager(new LinearLayoutManager(ChiTietDonHangActivity.this));
                    binding.recycleviewMathang.setAdapter(orderItemAdapter);
                    orderItemAdapter.notifyDataSetChanged();
                    orderItemAdapter.setOnItemClickListener((OrderItemAdapter.OnItemClickListener) ChiTietDonHangActivity.this);
                } else
                    Toast.makeText(ChiTietDonHangActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<FullOrderItem>> call, Throwable t) {
                Toast.makeText(ChiTietDonHangActivity.this, "Gọi API OI thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(FullOrderItem orderItem) {
        Intent intent = new Intent(this, DanhGiaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderItem", orderItem);
        intent.putExtras(bundle);
        startActivity(intent);
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
            startActivity(new Intent(this, XemDonActivity.class));
        }
    }
}