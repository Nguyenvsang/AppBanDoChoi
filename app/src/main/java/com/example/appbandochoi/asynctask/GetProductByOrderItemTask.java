package com.example.appbandochoi.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.databinding.ObservableField;

import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Response;

public class GetProductByOrderItemTask extends AsyncTask<Integer, Void, Product> {
    private APIService apiService;
    public ObservableField<String> productImages = new ObservableField<>();
    public ObservableField<String> productName = new ObservableField<>();

    public GetProductByOrderItemTask(ObservableField<String> productImages, ObservableField<String> productName) {
        this.productImages = productImages;
        this.productName = productName;
    }

    @Override
    protected Product doInBackground(Integer... integers) {
        int orderItemID = integers[0];
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Product> call = apiService.getProductByOrderItem(orderItemID);
        try {
            Response<Product> response = call.execute();
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
    protected void onPostExecute(Product product) {
        if (product != null) {
            System.out.println(product);
            productName.set(product.getProductName());
            productImages.set(product.getImages());
        }
    }
}