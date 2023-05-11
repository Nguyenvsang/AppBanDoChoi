package com.example.appbandochoi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CategoryAdapter;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.databinding.ActivityHomeBinding;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener, View.OnClickListener, View.OnTouchListener {
        private APIService apiService;
        private ActivityHomeBinding binding;
        private CategoryAdapter categoryAdapter;
        private List<Category> categoryList;
        private Context context;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = this;

            categoryAdapter = new CategoryAdapter(categoryList);

            getCategoryList();

            binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
            binding.setHome(this);

            binding.editTextSearch.setOnClickListener(this);
            binding.editTextSearch.setOnTouchListener(this);
        }

        public void getCategoryList() {
            apiService = RetrofitClient.getRetrofit().create(APIService.class);
            apiService.getCategoryList().enqueue(new Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    if (response.isSuccessful()) {
                        categoryList = response.body();
                        categoryAdapter = new CategoryAdapter(categoryList);
                        binding.rcDanhmuc.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                        binding.rcDanhmuc.setAdapter(categoryAdapter);
                        categoryAdapter.notifyDataSetChanged();
                        categoryAdapter.setOnItemClickListener((CategoryAdapter.OnItemClickListener) HomeActivity.this);
                    }
                    else
                        Toast.makeText(HomeActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                    System.out.println(t.toString());
                }
            });
        }

        @Override
        public void itemClick(Category category) {
            Intent intent = new Intent(this, DanhSachSPActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("category", category);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    @Override
    public void onClick(View v) {
        if (v.equals(binding.editTextSearch)) {
            finish();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(binding.editTextSearch)) {
            finish();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
