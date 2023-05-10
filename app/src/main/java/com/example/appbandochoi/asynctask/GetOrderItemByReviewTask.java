package com.example.appbandochoi.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.databinding.ObservableField;

import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GetOrderItemByReviewTask extends AsyncTask<Integer, Void, OrderItem> {
    private APIService apiService;
    private ObservableField<String> quantity = new ObservableField<>();

    public GetOrderItemByReviewTask(ObservableField<String> quantity) {
        this.quantity = quantity;
    }

    @Override
    protected OrderItem doInBackground(Integer... integers) {
        int reviewID = integers[0];
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<OrderItem> call = apiService.getOrderItemByReview(reviewID);
        try {
            Response<OrderItem> response = call.execute();
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
    protected void onPostExecute(OrderItem orderItem) {
        if (orderItem != null) {
            quantity.set(String.valueOf(orderItem.getQuantity()));
        }
    }
}

