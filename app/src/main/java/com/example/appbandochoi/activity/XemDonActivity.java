package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemDonActivity extends AppCompatActivity implements OrderAdapter.OnItemClickListener{

    private APIService apiService;
    private ActivityXemDonBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        orderAdapter = new OrderAdapter(orderList, context);

        getMyOrders();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_xem_don);
        binding.setOrder(this);
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

}