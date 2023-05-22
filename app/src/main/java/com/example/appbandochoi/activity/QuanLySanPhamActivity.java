package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.ProductManagementAdapter;
import com.example.appbandochoi.databinding.ActivityQuanLySanPhamBinding;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuanLySanPhamActivity extends AppCompatActivity implements ProductManagementAdapter.OnItemClickListener, View.OnClickListener {
    private APIService apiService;
    private ActivityQuanLySanPhamBinding binding;
    private ProductManagementAdapter productManagementAdapter;
    private List<Product> productList;
    private Context context;
    private Category category;
    private AutoCompleteTextView autoCompleteSort;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        productManagementAdapter = new ProductManagementAdapter(productList, context);

        if (bundle == null)
            getProductList();
        else {
            category = (Category) bundle.getSerializable("category");
            getProductListByCategory(category.getCategoryID());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quan_ly_san_pham);
        binding.setProduct(this);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        // Sort list options
        String[] sorts = getResources().getStringArray(R.array.sort);
        // Create an array adapter and pass the required parameters
        // In our case, pass the context, drop-down layout, and array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_sort, sorts);
        // Get reference to the AutoCompleteTextView
        autoCompleteSort = binding.autoCompleteSort;
        // Set the adapter to the AutoCompleteTextView
        autoCompleteSort.setAdapter(arrayAdapter);
        // Click
        autoCompleteSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Compare the selected item with each item to determine which one was clicked
                if (bundle == null) {
                    if (selectedItem.equals("Tên A-Z")) {
                        getProductListAndSort(1);
                    } else if (selectedItem.equals("Tên Z-A")) {
                        getProductListAndSort(2);
                    } else if (selectedItem.equals("Giá thấp - cao")) {
                        getProductListAndSort(3);
                    } else if (selectedItem.equals("Giá cao - thấp")) {
                        getProductListAndSort(4);
                    } else if (selectedItem.equals("Tất cả")) {
                        getProductListAndSort(0);
                    }
                }
            }
        });
    }

    public void anhXa() {
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
        imgBack = binding.imgBack;
    }

    public void getProductList() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductList().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(QuanLySanPhamActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productManagementAdapter = new ProductManagementAdapter(productList, context);
                    binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(QuanLySanPhamActivity.this));
                    binding.recycleviewSp.setAdapter(productManagementAdapter);
                    productManagementAdapter.notifyDataSetChanged();
                    productManagementAdapter.setOnItemClickListener((ProductManagementAdapter.OnItemClickListener) QuanLySanPhamActivity.this);
                    System.out.println(productList.size());
                } else
                    Toast.makeText(QuanLySanPhamActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(QuanLySanPhamActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
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

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(QuanLySanPhamActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productManagementAdapter = new ProductManagementAdapter(productList, context);
                    binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(QuanLySanPhamActivity.this));
                    binding.recycleviewSp.setAdapter(productManagementAdapter);
                    productManagementAdapter.notifyDataSetChanged();
                    productManagementAdapter.setOnItemClickListener((ProductManagementAdapter.OnItemClickListener) QuanLySanPhamActivity.this);
                } else
                    Toast.makeText(QuanLySanPhamActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(QuanLySanPhamActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getProductListAndSort(int sort) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductAndSort(sort).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(QuanLySanPhamActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productManagementAdapter = new ProductManagementAdapter(productList, context);
                    binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(QuanLySanPhamActivity.this));
                    binding.recycleviewSp.setAdapter(productManagementAdapter);
                    productManagementAdapter.notifyDataSetChanged();
                    productManagementAdapter.setOnItemClickListener((ProductManagementAdapter.OnItemClickListener) QuanLySanPhamActivity.this);
                } else
                    Toast.makeText(QuanLySanPhamActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(QuanLySanPhamActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(this, NguoiQuanLyActivity.class));
        }
    }
}