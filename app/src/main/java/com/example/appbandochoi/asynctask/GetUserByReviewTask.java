package com.example.appbandochoi.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GetUserByReviewTask extends AsyncTask<Integer, Void, User> {
    private APIService apiService;
    private ObservableField<String> reviewerName = new ObservableField<>();

    public GetUserByReviewTask(ObservableField<String> reviewerName) {
        this.reviewerName = reviewerName;
    }

    @Override
    protected User doInBackground(Integer... integers) {
        int reviewID = integers[0];
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<User> call = apiService.getUserByReview(reviewID);
        try {
            Response<User> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                int statusCode = response.code();
                System.out.println(String.valueOf(statusCode));
                return null;
            }
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
            System.out.println("Gọi API U thất bại!");
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        if (user != null) {
            reviewerName.set(user.getFirstname() + "***");
        }
    }
}

