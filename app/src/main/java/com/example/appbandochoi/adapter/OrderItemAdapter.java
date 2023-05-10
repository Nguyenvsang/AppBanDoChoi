package com.example.appbandochoi.adapter;

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
import com.example.appbandochoi.asynctask.GetProductByOrderItemTask;
import com.example.appbandochoi.asynctask.GetReviewByOrderItemTask;
import com.example.appbandochoi.asynctask.GetUserByReviewTask;
import com.example.appbandochoi.databinding.ItemChitietDonhangBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder> {
    private List<OrderItem> orderItemList;
    private OrderItemAdapter.OnItemClickListener onItemClickListener;
    private CardView cardViewReview;

    public OrderItemAdapter(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
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
        public ItemChitietDonhangBinding itemChitietDonhangBinding;
        private OnItemClickListener onItemClickListener;
        private OrderItem orderItem;

        public MyViewHolder(ItemChitietDonhangBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemChitietDonhangBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);

            cardViewReview = itemView.getRoot().findViewById(R.id.cardViewReview);
        }

        public void setBinding(OrderItem orderItem, int position) {
            if (itemChitietDonhangBinding.getViewHolder() == null) {
                itemChitietDonhangBinding.setViewHolder(this);
            }

            this.orderItem = orderItem;

            GetProductByOrderItemTask productTask = new GetProductByOrderItemTask(productImages, productName);
            productTask.execute(orderItem.getOrderItemID());

            GetReviewByOrderItemTask reviewTask = new GetReviewByOrderItemTask(reviewImages, star, reviewDate, comment, reviewID);
            reviewTask.execute(orderItem.getOrderItemID());

            if (reviewDate != null) {
                GetUserByReviewTask userTask = new GetUserByReviewTask(reviewerName);
                userTask.execute(reviewID);
            } else {
                cardViewReview.setVisibility(View.GONE);
            }

            price.set(String.valueOf(orderItem.getPrice()) + " VNĐ   x   " + String.valueOf(orderItem.getQuantity()));

        }

        public void onClick (View v){
            this.onItemClickListener.itemClick(orderItem);
        }
    }

    public void setOnItemClickListener(OrderItemAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(OrderItem orderItem);
    }
}