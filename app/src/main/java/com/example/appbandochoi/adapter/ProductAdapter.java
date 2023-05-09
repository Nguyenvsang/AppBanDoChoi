package com.example.appbandochoi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemSanphamBinding;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;
    private APIService apiService;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSanphamBinding itemSanphamBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_sanpham, parent, false);
        return new MyViewHolder(itemSanphamBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        holder.setBinding(productList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (productList == null)
            return 0;
        return productList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> productName = new ObservableField<>();
        public ObservableField<String> price = new ObservableField<>();
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<String> images = new ObservableField<>();
        public ObservableField<String> description = new ObservableField<>();
        public ItemSanphamBinding itemSanphamBinding;
        private OnItemClickListener onItemClickListener;
        private Product product;

        public MyViewHolder(ItemSanphamBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemSanphamBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(Product product, int position) {
            if (itemSanphamBinding.getViewHolder() == null) {
                itemSanphamBinding.setViewHolder(this);
            }
            //itemCartItemBinding.executePendingBindings();
            this.product = product;
            productName.set(product.getProductName());
            price.set(String.valueOf(product.getPrice()));
            quantity.set(String.valueOf(product.getQuantity()));
            images.set(product.getImages());
            description.set(product.getDescription());
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(product);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Product product);
    }
}
