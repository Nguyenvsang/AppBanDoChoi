package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.ProductAdapter;
import com.example.appbandochoi.databinding.ActivityDanhSachSpBinding;
import com.example.appbandochoi.databinding.ActivitySearchBinding;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    private APIService apiService;
    private ActivitySearchBinding binding;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Context context;
    private AutoCompleteTextView autoCompleteSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent intent = getIntent();
        String searchString = intent.getStringExtra("searchString");

        productAdapter = new ProductAdapter(productList);

        searchForProduct(searchString);
        binding.edtsearch.setText(searchString);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setSearch(this);

        // Search
        binding.edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchForProduct(binding.edtsearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Sort list options
        String[] sorts = getResources().getStringArray(R.array.sort);
        // Create an array adapter and pass the required parameters
        // In our case, pass the context, drop-down layout, and array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_sort, sorts);
        // Get reference to the AutoCompleteTextView
        autoCompleteSort = binding.autoCompleteSort;
        // Set the adapter to the AutoCompleteTextView
        autoCompleteSort.setAdapter(arrayAdapter);
        // Click
        autoCompleteSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchString = binding.edtsearch.getText().toString();
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Compare the selected item with each item to determine which one was clicked
                if (selectedItem.equals("Tên A-Z")) {
                    searchForProductAndSort(searchString, 1);
                } else if (selectedItem.equals("Tên Z-A")) {
                    searchForProductAndSort(searchString, 2);
                } else if (selectedItem.equals("Giá thấp - cao")) {
                    searchForProductAndSort(searchString, 3);
                } else if (selectedItem.equals("Giá cao - thấp")) {
                    searchForProductAndSort(searchString, 4);
                } else {
                    searchForProductAndSort(searchString, 0);
                }
            }
        });
    }

    public void searchForProduct(String searchString) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.searchProduct(searchString).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(SearchActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productAdapter = new ProductAdapter(productList);
                    binding.recycleviewSearch.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                    binding.recycleviewSearch.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) SearchActivity.this);
                } else
                    Toast.makeText(SearchActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void searchForProductAndSort(String searchString, int sort) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.searchProductAndSort(searchString, sort).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();

                    if (productList.size() == 0 || productList == null)
                        Toast.makeText(SearchActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();

                    productAdapter = new ProductAdapter(productList);
                    binding.recycleviewSearch.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                    binding.recycleviewSearch.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) SearchActivity.this);
                } else
                    Toast.makeText(SearchActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public void itemClick(Product product) {
        Intent intent = new Intent(this, ChiTietActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}