package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CartItemAdapter;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.adapter.ReviewAdapter;
import com.example.appbandochoi.databinding.ActivityChiTietBinding;
import com.example.appbandochoi.databinding.ItemDanhgiaBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        product = (Product) bundle.getSerializable("product");

        reviewAdapter = new ReviewAdapter(reviewList);

        getReviewList(product.getProductID());
        //getReviewList(1);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chi_tiet);
        binding.setReview(this);
        binding.setDetail(product);

        binding.imgDecreaseQuantity.setOnClickListener(ChiTietActivity.this);
        binding.imgIncreaseQuantity.setOnClickListener(ChiTietActivity.this);
        binding.btnthemvaogiohang.setOnClickListener(ChiTietActivity.this);
    }

    public void getReviewList(int productID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getReviewListByProduct(productID).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                System.out.println(response.body().toString());
                if (response.isSuccessful()) {
                    reviewList = response.body();
                    System.out.println(reviewList.size());
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

    @Override
    public void itemClick(Review review) {

    }

    @Override
    public void onClick(View view) {
        int productQuantity = product.getQuantity();
        int detailQuantity = Integer.parseInt(binding.txtSoluongDeThem.getText().toString());
        if (view.equals(binding.btnthemvaogiohang)) {

        }
        if (view.equals(binding.imgDecreaseQuantity)) {
            if (detailQuantity > 1)
                binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity - 1));
        }
        if (view.equals(binding.imgIncreaseQuantity)) {
            if (detailQuantity < productQuantity)
                binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity + 1));
        }
//        if (view.equals(binding.imgDecreaseQuantity)) {
//            binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity - 1));
//            binding.imgIncreaseQuantity.setEnabled(true);
//            if (detailQuantity == 0) {
//                binding.imgDecreaseQuantity.setEnabled(false);
//                binding.btnthemvaogiohang.setEnabled(false);
//            } else {
//                binding.imgDecreaseQuantity.setEnabled(true);
//                binding.btnthemvaogiohang.setEnabled(true);
//            }
//        }
//        if (view.equals(binding.imgIncreaseQuantity)) {
//            binding.txtSoluongDeThem.setText(String.valueOf(detailQuantity + 1));
//            binding.imgDecreaseQuantity.setEnabled(true);
//            binding.btnthemvaogiohang.setEnabled(true);
//            if (detailQuantity == productQuantity)
//                binding.imgIncreaseQuantity.setEnabled(false);
//        }
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