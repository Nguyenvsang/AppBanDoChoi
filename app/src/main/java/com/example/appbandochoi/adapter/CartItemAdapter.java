package com.example.appbandochoi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemGiohangBinding;
import com.example.appbandochoi.model.CartItem;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {
    private List<CartItem> cartItemList;
    private OnItemClickListener onItemClickListener;

    public CartItemAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
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
    }


    @Override
    public int getItemCount() {
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
            this.cartItem = cartItem;
            productName.set(cartItem.getProduct().getProductName());
            price.set(String.valueOf(cartItem.getProduct().getPrice()));
            quantity.set(String.valueOf(cartItem.getQuantity()));
            images.set(Constants.ROOT_URL.concat(cartItem.getProduct().getImages()));
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
}
