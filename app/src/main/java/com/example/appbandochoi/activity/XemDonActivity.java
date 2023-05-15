package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.OrderAdapter;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.databinding.ActivityXemDonBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemDonActivity extends AppCompatActivity implements OrderAdapter.OnItemClickListener, View.OnClickListener {
    private APIService apiService;
    private ActivityXemDonBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Context context;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;
    private TextView txtdonhangtrong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        orderAdapter = new OrderAdapter(orderList, context);

        getMyOrders();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_xem_don);
        binding.setOrder(this);

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
        txtdonhangtrong = binding.txtdonhangtrong;
    }

    public void getMyOrders() {
        User thisUser = new User();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                thisUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getMyOrder(thisUser.getUserID()).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(orderList, context);
                    binding.recycleviewDonhang.setLayoutManager(new LinearLayoutManager(XemDonActivity.this));
                    binding.recycleviewDonhang.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                    orderAdapter.setOnItemClickListener((OrderAdapter.OnItemClickListener) XemDonActivity.this);
                    if (orderList == null || orderList.size() == 0)
                        binding.txtdonhangtrong.setVisibility(View.VISIBLE);
                    else
                        binding.txtdonhangtrong.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(XemDonActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(XemDonActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(Order order) {
        Intent intent = new Intent(this, ChiTietDonHangActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
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
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}