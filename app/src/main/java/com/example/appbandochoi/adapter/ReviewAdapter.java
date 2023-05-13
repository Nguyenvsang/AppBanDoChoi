package com.example.appbandochoi.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.activity.ThanhToanActivity;
import com.example.appbandochoi.asynctask.GetOrderItemByReviewTask;
import com.example.appbandochoi.asynctask.GetUserByReviewTask;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemDanhgiaBinding;
import com.example.appbandochoi.databinding.ItemSanphamBinding;
import com.example.appbandochoi.model.CartItem;
import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.DateUtil;
import com.example.appbandochoi.utils.ShortDateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private List<Review> reviewList;
    private OnItemClickListener onItemClickListener;
    private APIService apiService;
    private User user;
    private OrderItem orderItem;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDanhgiaBinding itemDanhgiaBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_danhgia, parent, false);
        return new MyViewHolder(itemDanhgiaBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position){
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
        public ObservableField<String> images = new ObservableField<>();
        public ObservableField<String> star = new ObservableField<>();
        public ObservableField<String> reviewDate = new ObservableField<>();
        public ObservableField<String> comment = new ObservableField<>();
        public ItemDanhgiaBinding itemDanhgiaBinding;
        private OnItemClickListener onItemClickListener;
        private Review review;

        public MyViewHolder(ItemDanhgiaBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemDanhgiaBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(Review review, int position){
            if (itemDanhgiaBinding.getViewHolder() == null) {
                itemDanhgiaBinding.setViewHolder(this);
            }
            this.review = review;
            //getOrderItemByReview(review.getReviewID());

            GetUserByReviewTask task = new GetUserByReviewTask(reviewerName);
            task.execute(review.getReviewID());

            //reviewerName.set(review.getUser().getLastname());
            Timestamp date = review.getUpdatedAt() == null ? review.getCreatedAt() : review.getUpdatedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            reviewDate.set(new ShortDateUtil().parseShortDate(date));
            images.set(review.getImages());
            comment.set(review.getComment());
            star.set(String.valueOf(review.getStar()));

            GetOrderItemByReviewTask task2 = new GetOrderItemByReviewTask(quantity);
            task2.execute(review.getReviewID());
        }
        public void onClick(View v) {
            this.onItemClickListener.itemClick(review);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Review review);
    }

    public void getUserByReview(int reviewID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUserByReview(reviewID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                } else {
                    int statusCode = response.code();
                    System.out.println(String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", t.toString());
                System.out.println("Gọi API U thất bại!");
            }
        });
    }

    public void getOrderItemByReview(int reviewID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderItemByReview(reviewID).enqueue(new Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, Response<OrderItem> response) {
                if (response.isSuccessful()) {
                    orderItem = response.body();
                } else {
                    int statusCode = response.code();
                    System.out.println(String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                Log.e("ERROR", t.toString());
                System.out.println("Gọi API OI thất bại!");
            }
        });
    }

}
