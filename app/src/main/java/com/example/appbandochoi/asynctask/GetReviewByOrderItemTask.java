package com.example.appbandochoi.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.databinding.ObservableField;

import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Response;

public class GetReviewByOrderItemTask extends AsyncTask<Integer, Void, Review> {
    private APIService apiService;
    public ObservableField<String> reviewImages = new ObservableField<>();
    public ObservableField<String> star = new ObservableField<>();
    public ObservableField<String> reviewDate = new ObservableField<>();
    public ObservableField<String> comment = new ObservableField<>();
    public int reviewID;
    public boolean isNull = true;

    public GetReviewByOrderItemTask(ObservableField<String> reviewImages, ObservableField<String> star,
                                    ObservableField<String> reviewDate, ObservableField<String> comment, int reviewID, boolean isNull) {
        this.reviewImages = reviewImages;
        this.star = star;
        this.reviewDate = reviewDate;
        this.comment = comment;
        this.reviewID = reviewID;
        this.isNull = isNull;
    }

    @Override
    protected Review doInBackground(Integer... integers) {
        int orderItemID = integers[0];
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Review> call = apiService.getReviewByOrderItem(orderItemID);
        try {
            Response<Review> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                int statusCode = response.code();
                System.out.println(response.errorBody().toString());
                return null;
            }
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
            System.out.println("Gọi API U thất bại!");
            return null;
        }
    }

    @Override
    protected void onPostExecute(Review review) {
        if (review != null) {
            System.out.println(review);
            Timestamp date = review.getUpdatedAt() == null ? review.getCreatedAt() : review.getUpdatedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            reviewDate.set(new ShortDateUtil().parseShortDate(date));
            reviewImages.set(review.getImages());
            comment.set(review.getComment());
            star.set(String.valueOf(review.getStar()));
            reviewID = review.getReviewID();
            isNull = false;
        }
    }
}
