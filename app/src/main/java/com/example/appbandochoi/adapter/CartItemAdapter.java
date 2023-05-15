package com.example.appbandochoi.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.HalfFloat;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.activity.GioHangActivity;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemGiohangBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {
    private List<CartItem> cartItemList;
    private OnItemClickListener onItemClickListener;
    private APIService apiService;
    private final TextView txttongtien;

    public CartItemAdapter(List<CartItem> cartItemList, TextView txttongtien) {
        this.cartItemList = cartItemList;
        this.txttongtien = txttongtien;
    }

    @NonNull
    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGiohangBinding itemCartItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_giohang, parent, false);
        return new MyViewHolder(itemCartItemBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.MyViewHolder holder, int position) {
        holder.setBinding(cartItemList.get(position), position);
        // Increase item's quantity
        holder.itemCartItemBinding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartItem cartItem = (CartItem) cartItemList.get(holder.getAdapterPosition());
                // The quantity is maximal
                if (cartItem.getQuantity() == cartItem.getProduct().getQuantity()) {

                } else {
                    updateCart(cartItem, 1);
                }
            }
        });
        // Decrease item's quantity
        holder.itemCartItemBinding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartItem cartItem = (CartItem) cartItemList.get(holder.getAdapterPosition());
                // The quantity is 1
                if (cartItem.getQuantity() == 1) {
                    //delete
                    deleteCartItem(cartItem.getCartItemID());
                } else {
                    updateCart(cartItem, -1);
                }
            }
        });
        // Delete
        holder.itemCartItemBinding.btnXoaMatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = (CartItem) cartItemList.get(holder.getAdapterPosition());
                //delete
                deleteCartItem(cartItem.getCartItemID());
            }
        });
    }


    @Override
    public int getItemCount() {
        if (cartItemList == null)
            return 0;
        return cartItemList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> productName = new ObservableField<>();
        public ObservableField<String> price = new ObservableField<>();
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<String> images = new ObservableField<>();
        public ObservableField<String> total = new ObservableField<>();
        public ItemGiohangBinding itemCartItemBinding;
        private OnItemClickListener onItemClickListener;
        private CartItem cartItem;

        public MyViewHolder(ItemGiohangBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemCartItemBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(CartItem cartItem, int position) {
            if (itemCartItemBinding.getViewHolder() == null) {
                itemCartItemBinding.setViewHolder(this);
            }
            //itemCartItemBinding.executePendingBindings();
            this.cartItem = cartItem;
            productName.set(cartItem.getProduct().getProductName());
            price.set(String.valueOf(cartItem.getProduct().getPrice()));
            quantity.set(String.valueOf(cartItem.getQuantity()));
            images.set(cartItem.getProduct().getImages());
            total.set(String.valueOf(cartItem.getProduct().getPrice() * cartItem.getQuantity()));
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(cartItem);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(CartItem cartItem);
    }

    public void updateCart (CartItem cartItemModel, int quantity) {
        // Create requestbody
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("cartItemID", cartItemModel.getCartItemID());
        newMap.put("quantity", cartItemModel.getQuantity() + quantity);
        Gson gson = new Gson();
        String json = gson.toJson(newMap);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateCart(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject cart;
                try {
                    String responseBody = response.body().string();
                    cart = new JSONObject(responseBody);
                    JSONArray cartItems = cart.getJSONArray("cartItems");
                    // Reset cartItemList
                     cartItemList.clear();
                    // Lấy danh sách cart
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
                        notifyDataSetChanged();
                        updateTotalPrice();
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                //Toast.makeText(context, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCartItem (int cartItemID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.deleteCartItem(cartItemID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject cart;
                try {
                    String responseBody = response.body().string();
                    cart = new JSONObject(responseBody);
                    JSONArray cartItems = cart.getJSONArray("cartItems");
                    // Reset cartItemList
                    cartItemList.clear();
                    // Lấy danh sách cart
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
                        notifyDataSetChanged();
                        updateTotalPrice();
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                //Toast.makeText(context, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotalPrice() {
        long totalPrice = 0;
        for (CartItem item : cartItemList) {
            totalPrice += item.getQuantity() * item.getProduct().getPrice();
        }
        txttongtien.setText(String.valueOf(totalPrice).concat(" VNĐ"));
    }
}
