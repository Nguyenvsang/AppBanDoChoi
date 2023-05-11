package com.example.appbandochoi.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appbandochoi.activity.DanhSachSPActivity;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Response;

public class GetProductListTask extends AsyncTask<Void, Void, List<Product>> {

    private Context context;
    private ProductAdapter productAdapter;
    private ActivityDanhSachSpBinding binding;
    private APIService apiService;

    public GetProductListTask(Context context, ProductAdapter productAdapter, ActivityDanhSachSpBinding binding) {
        this.context = context.getApplicationContext();
        this.productAdapter = productAdapter;
        this.binding = binding;
    }
    @Override
    protected List<Product> doInBackground(Void... voids) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        try {
            Response<List<Product>> response = apiService.getProductListForSale().execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new Exception("Response is not successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Product> productList) {
        if (productList != null) {
            productAdapter = new ProductAdapter(productList);
            binding.recycleviewSp.setLayoutManager(new LinearLayoutManager(context));
            binding.recycleviewSp.setAdapter(productAdapter);
            productAdapter.notifyDataSetChanged();
            productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) context);
            System.out.println(productList.size());
        } else {
            Toast.makeText(context, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}

