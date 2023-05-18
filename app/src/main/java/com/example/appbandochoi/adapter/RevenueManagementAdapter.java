package com.example.appbandochoi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.databinding.ItemQuanLyDoanhThuBinding;
import com.example.appbandochoi.model.ItemProduct;
import com.example.appbandochoi.model.Order;

import java.util.List;

public class RevenueManagementAdapter extends RecyclerView.Adapter<RevenueManagementAdapter.MyViewHolder> {
    private List<ItemProduct> itemProductList;
    private RevenueManagementAdapter.OnItemClickListener onItemClickListener;

    public RevenueManagementAdapter(List<ItemProduct> itemProductList) {
        this.itemProductList = itemProductList;
    }

    @NonNull
    @Override
    public RevenueManagementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuanLyDoanhThuBinding itemQuanLyDoanhThuBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_quan_ly_doanh_thu, parent, false);
        return new RevenueManagementAdapter.MyViewHolder(itemQuanLyDoanhThuBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueManagementAdapter.MyViewHolder holder, int position) {
        holder.setBinding(itemProductList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (itemProductList == null)
            return 0;
        return itemProductList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<String> productImages = new ObservableField<>();
        public ObservableField<String> total = new ObservableField<>();
        public ObservableField<String> productName = new ObservableField<>();
        public ItemQuanLyDoanhThuBinding itemQuanLyDoanhThuBinding;
        private OnItemClickListener onItemClickListener;
        private ItemProduct itemProduct;

        public MyViewHolder(ItemQuanLyDoanhThuBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemQuanLyDoanhThuBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(ItemProduct itemProduct, int position) {
            if (itemQuanLyDoanhThuBinding.getViewHolder() == null) {
                itemQuanLyDoanhThuBinding.setViewHolder(this);
            }

            this.itemProduct = itemProduct;

            total.set(itemProduct.getTotal() + " VNĐ");
            quantity.set("Số lượng: " + itemProduct.getQuantity());
            productImages.set(itemProduct.getProductImage());
            productName.set(itemProduct.getProductName());
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(itemProduct);
        }
    }

    public void setOnItemClickListener(RevenueManagementAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Order order);

        void itemClick(ItemProduct itemProduct);
    }
}