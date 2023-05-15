package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CartItemAdapter;
import com.example.appbandochoi.adapter.OrderItemAdapter;
import com.example.appbandochoi.databinding.ActivityGioHangBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class GioHangActivity extends AppCompatActivity implements CartItemAdapter.OnItemClickListener, View.OnClickListener {
    private APIService apiService;
    private ActivityGioHangBinding binding;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private TextView txttongtien;
    private LinearLayout linearTrangchu, linearSanpham, linearDonhang, linearTaikhoan;
    private FloatingActionButton btnCart;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get userID
        User user = new User();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                user = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        cartItemAdapter = new CartItemAdapter(cartItemList, txttongtien);

        displayCart(user.getUserID());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gio_hang);
        binding.setCart(this);

        // Mapping
        anhXa();
        // Click action
        linearTrangchu.setOnClickListener(this);
        linearSanpham.setOnClickListener(this);
        linearDonhang.setOnClickListener(this);
        linearTaikhoan.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        txttongtien = binding.txttongtien;
        cartItemAdapter.setOnItemClickListener((CartItemAdapter.OnItemClickListener) this);
        binding.btmuahang.setOnClickListener(this);

        displayButton();
    }

    public void anhXa() {
        linearTrangchu = binding.linearTrangchu;
        linearSanpham = binding.linearSanpham;
        linearDonhang = binding.linearDonhang;
        linearTaikhoan = binding.linearTaikhoan;
        btnCart = binding.btnCart;
        imgBack = binding.imgBack;
    }

    public void displayButton() {
        if(cartItemList.isEmpty() || cartItemList == null) {
            // Disable the button
            binding.btmuahang.setEnabled(false);
            // Set the capacity (alpha value) of the button
            binding.btmuahang.setAlpha(0.5f);
        } else {
            // Disable the button
            binding.btmuahang.setEnabled(true);
            // Set the capacity (alpha value) of the button
            binding.btmuahang.setAlpha(1.0f);
        }
    }

    private void displayCart(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.displayCart(userID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject cart;
                try {
                    String responseBody = response.body().string();
                    cart = new JSONObject(responseBody);
                    JSONArray cartItems = cart.getJSONArray("cartItems");
                    // Lấy danh sách cart item
                    for (int i = 0; i < cartItems.length(); i++) {
                        JSONObject cartItem = cartItems.getJSONObject(i);
                        CartItem thisCartItem = new CartItem(
                                cartItem.getInt("cartItemID"),
                                cartItem.getInt("quantity"),
                                null
                        );
                        JSONObject product = cartItem.getJSONObject("product");
                        Product thisProduct = new Product(
                                product.getInt("productID"),
                                product.getString("productName"),
                                product.getString("description"),
                                product.getInt("quantity"),
                                product.getLong("price"),
                                product.getString("images"));
                        thisCartItem.setProduct(thisProduct);
                        cartItemList.add(thisCartItem);

                        // Set adapter
                        cartItemAdapter = new CartItemAdapter(cartItemList, txttongtien);
                        // binding
                        binding.recyclerviewgiohang.setLayoutManager(new LinearLayoutManager(GioHangActivity.this));
                        binding.recyclerviewgiohang.setAdapter(cartItemAdapter);
                        cartItemAdapter.notifyDataSetChanged();
                        displayButton();
                        cartItemAdapter.updateTotalPrice();
                        cartItemAdapter.setOnItemClickListener((CartItemAdapter.OnItemClickListener) GioHangActivity.this);
                    }
                    if (cartItemList == null || cartItemList.size() == 0)
                        binding.txtgiohangtrong.setVisibility(View.VISIBLE);
                    else
                        binding.txtgiohangtrong.setVisibility(View.GONE);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(GioHangActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void itemClick(CartItem cartItem) {
        Intent intent = new Intent(this, ChiTietActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", cartItem.getProduct());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.btmuahang)) {
            String total = txttongtien.getText().toString();
            finish();
            Intent intent = new Intent(this, ThanhToanActivity.class);
            intent.putExtra("totalOrder", Integer.parseInt(total.substring(0, total.length() - 4)));
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
        if (view.equals(imgBack)) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}