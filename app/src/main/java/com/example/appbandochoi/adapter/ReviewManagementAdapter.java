package com.example.appbandochoi.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.asynctask.ConvertImageFromURLTask;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemQuanLyDanhGiaBinding;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.util.List;

public class ReviewManagementAdapter extends RecyclerView.Adapter<ReviewManagementAdapter.MyViewHolder> {
    private List<Review> reviewList;
    private ReviewManagementAdapter.OnItemClickListener onItemClickListener;

    public ReviewManagementAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewManagementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuanLyDanhGiaBinding itemQuanLyDanhGiaBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_quan_ly_danh_gia, parent, false);
        return new ReviewManagementAdapter.MyViewHolder(itemQuanLyDanhGiaBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewManagementAdapter.MyViewHolder holder, int position) {
        holder.setBinding(reviewList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (reviewList == null)
            return 0;
        return reviewList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> reviewerName = new ObservableField<>();
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<Bitmap> reviewImages = new ObservableField<>();
        public ObservableField<String> star = new ObservableField<>();
        public ObservableField<String> reviewDate = new ObservableField<>();
        public ObservableField<String> updateDate = new ObservableField<>();
        public ObservableField<String> comment = new ObservableField<>();
        public ObservableField<Bitmap> productImages = new ObservableField<>();
        public ObservableField<String> price = new ObservableField<>();
        public ObservableField<String> productName = new ObservableField<>();
        public ItemQuanLyDanhGiaBinding itemQuanLyDanhGiaBinding;
        private ReviewManagementAdapter.OnItemClickListener onItemClickListener;
        private Review review;

        public MyViewHolder(ItemQuanLyDanhGiaBinding itemView, ReviewManagementAdapter.OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemQuanLyDanhGiaBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(Review review, int position) {
            if (itemQuanLyDanhGiaBinding.getViewHolder() == null) {
                itemQuanLyDanhGiaBinding.setViewHolder(this);
            }
            this.review = review;

            productName.set(review.getProduct().getProductName());
            ConvertImageFromURLTask productTask = new ConvertImageFromURLTask(productImages);
            productTask.execute(review.getProduct().getImages().contains("/images/product") ?
                    Constants.ROOT_URL + review.getProduct().getImages() :
                    review.getProduct().getImages());
            //productImages.set(review.getProduct().getImages());
            quantity.set("Số lượng: " + review.getOrderItem().getQuantity());
            price.set("Giá: " + review.getOrderItem().getPrice());

            reviewerName.set("Bởi: " + review.getUser().getFirstname() + "***");

            if (review.getCreatedAt() == null)
                reviewDate.set("Đánh giá vào: ");
            else
                reviewDate.set("Đánh giá vào: " + new ShortDateUtil().parseShortDate(review.getCreatedAt()));
            if (review.getCreatedAt() == null)
                updateDate.set("Cập nhật vào: ");
            else
                updateDate.set("Cập nhật vào: " + new ShortDateUtil().parseShortDate(review.getUpdatedAt()));
            if (review.getImages() != null) {
                ConvertImageFromURLTask reviewTask = new ConvertImageFromURLTask(reviewImages);
                reviewTask.execute(review.getImages().contains("/images/review") ?
                        Constants.ROOT_URL + review.getImages() :
                        review.getImages());
                //reviewImages.set(review.getImages());
            }
            comment.set(review.getComment());
            star.set(String.valueOf(review.getStar()));
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(review);
        }
    }

    public void setOnItemClickListener(ReviewManagementAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Review review);
    }

}
