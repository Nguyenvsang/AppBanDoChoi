package com.example.appbandochoi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CategoryAdapter;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.databinding.ActivityHomeBinding;
import com.example.appbandochoi.model.Category;
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

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener, View.OnClickListener, View.OnTouchListener, ProductAdapter.OnItemClickListener {
    private APIService apiService;
    private ActivityHomeBinding binding;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Context context;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgProfileHome;
    private TextView tvhi;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(HomeActivity.this, DangNhapActivity.class));
            finish();
        }

        categoryAdapter = new CategoryAdapter(categoryList);
        getCategoryList();

        productAdapter = new ProductAdapter(productList);
        getMostPopular();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setHome(this);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgProfileHome.setOnClickListener(this);
        binding.editTextSearch.setOnClickListener(this);
        binding.editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH  || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    finish();
                    Intent intent = new Intent();
                    intent.putExtra("searchString", binding.editTextSearch.getText().toString());
                    startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                    return true;
                }
                return false;
            }
        });

        User storedUser = null;
        // Display info
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                storedUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                startActivity(new Intent(HomeActivity.this, DangNhapActivity.class));
                finish();
            }
        }
        if (storedUser != null) {
            getUser(storedUser.getUserID());
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.profile).into(imgProfileHome);
            tvhi.setText("Xin chào");
        }

    }

    public void anhXa() {
        imgProfileHome = binding.imgProfileHome;
        tvhi = binding.tvhi;
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
    }

    public void getCategoryList() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getCategoryList().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categoryAdapter = new CategoryAdapter(categoryList);
                    binding.rcDanhmuc.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.rcDanhmuc.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    categoryAdapter.setOnItemClickListener((CategoryAdapter.OnItemClickListener) HomeActivity.this);
                } else
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

    public void getMostPopular() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getMostPopular().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(HomeActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productAdapter = new ProductAdapter(productList);
                    binding.rcPhobien.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    binding.rcPhobien.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productAdapter.setOnItemClickListener(HomeActivity.this);
                    System.out.println(productList.size());
                } else
                    Toast.makeText(HomeActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getUser(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUser(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    String image = "";
                    if (user.getImage() != null) {
                        if (user.getImage().contains("/images/profile"))
                            image = Constants.ROOT_URL.concat(user.getImage());
                        else
                            image = user.getImage();
                    }
                    Glide.with(getApplicationContext()).load(user.getImage() == null ? R.drawable.profile : image).into(imgProfileHome);
                    tvhi.setText("Xin chào " + user.getFirstname());
                } else {
                    int statusCode = response.code();
                    Toast.makeText(HomeActivity.this, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(HomeActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.editTextSearch)) {
            finish();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
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
        if (view.equals(imgProfileHome)) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
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

    @Override
    public void itemClick(Product product) {
        Intent intent = new Intent(this, ChiTietActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
