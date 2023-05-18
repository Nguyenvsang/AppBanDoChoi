package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.ReviewAdapter;
import com.example.appbandochoi.adapter.ReviewManagementAdapter;
import com.example.appbandochoi.databinding.ActivityQuanLyDanhGiaBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuanLyDanhGiaActivity extends AppCompatActivity implements ReviewManagementAdapter.OnItemClickListener {
    private APIService apiService;
    private ActivityQuanLyDanhGiaBinding binding;
    private ReviewManagementAdapter reviewManagementAdapter;
    private List<Review> reviewList;
    private Order order;
    private AutoCompleteTextView autoCompleteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        reviewManagementAdapter = new ReviewManagementAdapter(reviewList);
        getReviewDesc();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quan_ly_danh_gia);
        binding.setReview(this);

        // Filter options
        String[] filters = getResources().getStringArray(R.array.review);
        // Create an array adapter and pass the required parameters
        // In our case, pass the context, drop-down layout, and array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_sort, filters);
        // Get reference to the AutoCompleteTextView
        autoCompleteFilter = binding.autoCompleteFilter;
        // Set the adapter to the AutoCompleteTextView
        autoCompleteFilter.setAdapter(arrayAdapter);

        //Click
        autoCompleteFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Filter
                if (selectedItem.equals("1 sao")) {
                    getReviewByStar(1);
                } else if (selectedItem.equals("2 sao")) {
                    getReviewByStar(2);
                } else if (selectedItem.equals("3 sao")) {
                    getReviewByStar(3);
                } else if (selectedItem.equals(" 4 sao")) {
                    getReviewByStar(4);
                } else if (selectedItem.equals("5 sao")) {
                    getReviewByStar(5);
                } else if (selectedItem.equals("Tất cả")){
                    getReviewDesc();
                }

            }
        });
    }

    public void getReviewByStar(int star) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getReviewByStar(star).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    reviewList = response.body();
                    reviewManagementAdapter = new ReviewManagementAdapter(reviewList);
                    binding.recycleviewQldanhgia.setLayoutManager(new LinearLayoutManager(QuanLyDanhGiaActivity.this));
                    binding.recycleviewQldanhgia.setAdapter(reviewManagementAdapter);
                    reviewManagementAdapter.notifyDataSetChanged();
                    reviewManagementAdapter.setOnItemClickListener((ReviewManagementAdapter.OnItemClickListener) QuanLyDanhGiaActivity.this);
                } else
                    Toast.makeText(QuanLyDanhGiaActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(QuanLyDanhGiaActivity.this, "Gọi API R thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getReviewDesc() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getReviewDesc().enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    reviewList = response.body();
                    reviewManagementAdapter = new ReviewManagementAdapter(reviewList);
                    binding.recycleviewQldanhgia.setLayoutManager(new LinearLayoutManager(QuanLyDanhGiaActivity.this));
                    binding.recycleviewQldanhgia.setAdapter(reviewManagementAdapter);
                    reviewManagementAdapter.notifyDataSetChanged();
                    reviewManagementAdapter.setOnItemClickListener((ReviewManagementAdapter.OnItemClickListener) QuanLyDanhGiaActivity.this);
                } else
                    Toast.makeText(QuanLyDanhGiaActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(QuanLyDanhGiaActivity.this, "Gọi API R thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(Review review) {
        // Get order by order item from review
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderByOrderItem(review.getOrderItem().getOrderItemID()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    order = response.body();

                    Intent intent = new Intent(getApplicationContext(), ChiTietDonHangActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(QuanLyDanhGiaActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(QuanLyDanhGiaActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}