package com.example.appbandochoi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.activity.DanhGiaActivity;
import com.example.appbandochoi.databinding.ItemChitietDonhangBinding;
import com.example.appbandochoi.model.FullOrderItem;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.sql.Timestamp;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder> {
    private List<FullOrderItem> orderItemList;
    private OrderItemAdapter.OnItemClickListener onItemClickListener;
    private CardView cardViewReview;
    private Context context;
    private Button btndanhgia;

    public OrderItemAdapter(List<FullOrderItem> orderItemList, Context context) {
        this.orderItemList = orderItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChitietDonhangBinding itemChitietDonhangBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_chitiet_donhang, parent, false);
        return new OrderItemAdapter.MyViewHolder(itemChitietDonhangBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.MyViewHolder holder, int position) {
        holder.setBinding(orderItemList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (orderItemList == null)
            return 0;
        return orderItemList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> reviewerName = new ObservableField<>();
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<String> reviewImages = new ObservableField<>();
        public ObservableField<String> star = new ObservableField<>();
        public ObservableField<String> reviewDate = new ObservableField<>();
        public ObservableField<String> comment = new ObservableField<>();
        public ObservableField<String> productImages = new ObservableField<>();
        public ObservableField<String> price = new ObservableField<>();
        public ObservableField<String> productName = new ObservableField<>();
        public int reviewID;
        public boolean isNull;
        public ItemChitietDonhangBinding itemChitietDonhangBinding;
        private OnItemClickListener onItemClickListener;
        private FullOrderItem orderItem;

        public MyViewHolder(ItemChitietDonhangBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemChitietDonhangBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);

            cardViewReview = itemView.getRoot().findViewById(R.id.cardViewReview);
            btndanhgia = itemView.getRoot().findViewById(R.id.btnReview);
        }

        public void setBinding(FullOrderItem orderItem, int position) {
            if (itemChitietDonhangBinding.getViewHolder() == null) {
                itemChitietDonhangBinding.setViewHolder(this);
            }

            Button btnReview = itemChitietDonhangBinding.getRoot().findViewById(R.id.btnReview);

            this.orderItem = orderItem;

            price.set(orderItem.getQuantity() + "   X   " + orderItem.getPrice() + " VNĐ");
            productImages.set(orderItem.getProductImage());
            productName.set(orderItem.getProductName());
            if (orderItem.getCreatedAt() == null) {
                cardViewReview.setVisibility(View.GONE);
            } else {
                Timestamp date = orderItem.getUpdatedAt() == null ? orderItem.getCreatedAt() : orderItem.getUpdatedAt();
                reviewDate.set(new ShortDateUtil().parseShortDate(date));
                reviewImages.set(orderItem.getReviewImage());
                comment.set(orderItem.getComment() == null ? "Không có đánh giá!" : orderItem.getComment());
                star.set(String.valueOf(orderItem.getStar()));
                reviewerName.set(orderItem.getFirstname() + "***");
            }

            if (orderItem.isCompleted() && orderItem.getCreatedAt() == null)
                btndanhgia.setVisibility(View.VISIBLE);
            else
                btndanhgia.setVisibility(View.GONE);

            btndanhgia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DanhGiaActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderItem", orderItem);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }


        public void onClick(View v) {
            this.onItemClickListener.itemClick(orderItem);
        }
    }

    public void setOnItemClickListener(OrderItemAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(FullOrderItem orderItem);
    }
}