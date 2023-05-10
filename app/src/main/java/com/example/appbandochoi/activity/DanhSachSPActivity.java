package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.asynctask.GetProductListTask;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.databinding.ItemSanphamBinding;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachSPActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    private APIService apiService;
    private ActivityDanhSachSpBinding binding;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Context context;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        category = (Category) bundle.getSerializable("category");

        productAdapter = new ProductAdapter(productList);

//        GetProductListTask task = new GetProductListTask(context, productAdapter, binding);
//        task.execute();
        if (category == null)
            getProductList();
        else
            getProductListByCategory(category.getCategoryID());


        binding = DataBindingUtil.setContentView(this, R.layout.activity_danh_sach_sp);
        binding.setProduct(this);
    }

    public void getProductList() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductList().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();
                    productAdapter = new ProductAdapter(productList);
                    binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(DanhSachSPActivity.this));
                    binding.recycleviewSp.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) DanhSachSPActivity.this);
                    System.out.println(productList.size());
                }
                else
                    Toast.makeText(DanhSachSPActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(DanhSachSPActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getProductListByCategory(int categoryID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductByCategory(categoryID).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();
                    productAdapter = new ProductAdapter(productList);
                    binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(DanhSachSPActivity.this));
                    binding.recycleviewSp.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) DanhSachSPActivity.this);
                }
                else
                    Toast.makeText(DanhSachSPActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(DanhSachSPActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(Product product) {
        Intent intent = new Intent(this, ChiTietActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}