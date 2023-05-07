package com.example.appbandochoi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.CartItemAdapter;
import com.example.appbandochoi.databinding.ActivityGioHangBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;

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

public class GioHangActivity extends AppCompatActivity implements CartItemAdapter.OnItemClickListener{
    private APIService apiService;
    private ActivityGioHangBinding binding;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private TextView txttongtien;

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
        txttongtien = binding.txttongtien;
        cartItemAdapter.setOnItemClickListener((CartItemAdapter.OnItemClickListener) this);
        binding.btmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(GioHangActivity.this, ThanhToanActivity.class);
                // Lưu lại tổng giá trị và chuyển đi
                String totalOrder = txttongtien.getText().toString();
                totalOrder = totalOrder.substring(0, totalOrder.length() - 4);
                intent.putExtra("totalOrder", Integer.parseInt(totalOrder));
                startActivity(intent);
            }
        });
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
                        cartItemAdapter.updateTotalPrice();
                    }
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
        Toast.makeText(this, "You've clicked!", Toast.LENGTH_SHORT).show();
    }

}