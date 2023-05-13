package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CartItemAdapter;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.adapter.ReviewAdapter;
import com.example.appbandochoi.adapter.SliderAdapter;
import com.example.appbandochoi.databinding.ActivityChiTietBinding;
import com.example.appbandochoi.databinding.ItemDanhgiaBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Image;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietActivity extends AppCompatActivity implements ReviewAdapter.OnItemClickListener, View.OnClickListener {

    private APIService apiService;
    private ActivityChiTietBinding binding;
    private Product product;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private SliderAdapter sliderAdapter;
    private SliderView sliderView;
    private List<Image> imageList;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageList = new ArrayList<>();

        // Get data
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        product = (Product) bundle.getSerializable("product");

        reviewAdapter = new ReviewAdapter(reviewList);

        getReviewList(product.getProductID());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chi_tiet);
        binding.setReview(this);
        binding.setDetail(product);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        if (product.getQuantity() == 0) {
            // Disable the button
            binding.btnthemvaogiohang.setEnabled(false);
            // Set the capacity (alpha value) of the button
            binding.btnthemvaogiohang.setAlpha(0.5f);
        }

        sliderView = binding.imageSlider;
        getImageList(product.getProductID());

        binding.imgDecreaseQuantity.setOnClickListener(ChiTietActivity.this);
        binding.imgIncreaseQuantity.setOnClickListener(ChiTietActivity.this);
        binding.btnthemvaogiohang.setOnClickListener(ChiTietActivity.this);
    }

    public void anhXa() {
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
        imgBack = binding.imgBack;
    }

    public void getReviewList(int productID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getReviewListByProduct(productID).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                System.out.println(response.body().toString());
                if (response.isSuccessful()) {
                    reviewList = response.body();
                    if (reviewList == null || reviewList.size() == 0) {
                        binding.recycleviewDanhgia.setVisibility(View.GONE);
                    }
                    reviewAdapter = new ReviewAdapter(reviewList);
                    binding.recycleviewDanhgia.setLayoutManager(new LinearLayoutManager(ChiTietActivity.this));
                    binding.recycleviewDanhgia.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                    reviewAdapter.setOnItemClickListener((ReviewAdapter.OnItemClickListener) ChiTietActivity.this);
                } else
                    Toast.makeText(ChiTietActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(ChiTietActivity.this, "Gọi API R thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getImageList(int productID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getImagesForProduct(productID).enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                System.out.println(response.body().toString());
                if (response.isSuccessful()) {
                    imageList = response.body();
                    sliderAdapter = new SliderAdapter(getApplicationContext(), imageList);
                    sliderView.setSliderAdapter(sliderAdapter);
                    sliderView.setIndicatorAnimation (IndicatorAnimationType.WORM);
                    sliderView.setAutoCycleDirection (SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                    sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.red));
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.startAutoCycle();
                    sliderView.setScrollTimeInSec(5);
                } else
                    Toast.makeText(ChiTietActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Toast.makeText(ChiTietActivity.this, "Gọi API R thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(Review review) {

    }

    @Override
    public void onClick(View view) {
        User thisUser = new User();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                thisUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        // Total quantity
        int productQuantity = product.getQuantity();
        // Quantity in textview
        int detailQuantity = Integer.parseInt(binding.txtSoluongDeThem.getText().toString());

        if (view.equals(binding.btnthemvaogiohang)) {
            addToCart(product.getProductID(), detailQuantity, thisUser.getUserID());
        }
        if (view.equals(binding.imgDecreaseQuantity)) {
            if (detailQuantity > 1)
                binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity - 1));
        }
        if (view.equals(binding.imgIncreaseQuantity)) {
            if (detailQuantity < productQuantity)
                binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity + 1));
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
        if (view.equals(imgBack)) {
            finish();
            startActivity(new Intent(this, DanhSachSPActivity.class));
        }
    }

    public void addToCart(int productID, int quantity, int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.addToCart(productID, quantity, userID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                    Toast.makeText(ChiTietActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(ChiTietActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}