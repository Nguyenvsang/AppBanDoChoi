package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.RevenueManagementAdapter;
import com.example.appbandochoi.databinding.ActivityQuanLyDoanhThuBinding;
import com.example.appbandochoi.fragment.MonthYearPickerDialog;
import com.example.appbandochoi.model.ItemProduct;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuanLyDoanhThuActivity extends AppCompatActivity
        implements RevenueManagementAdapter.OnItemClickListener, DatePickerDialog.OnDateSetListener, MonthYearPickerDialog.OnMonthYearSetListener, View.OnClickListener {

    private APIService apiService;
    private ActivityQuanLyDoanhThuBinding binding;
    private RevenueManagementAdapter revenueManagementAdapter;
    private List<ItemProduct> itemProducts;
    private Context context;
    private int month, year;
    MonthYearPickerDialog pd = new MonthYearPickerDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        pd.setListener(this, this, this);

        revenueManagementAdapter = new RevenueManagementAdapter(itemProducts);
        getRevenueByProduct(0, 0);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quan_ly_doanh_thu);
        binding.setRevenue(this);

        binding.pickerMonth.setText(String.valueOf(month));
        binding.pickerYear.setText(String.valueOf(year));

        binding.pickerMonth.setOnClickListener(this);
        binding.pickerYear.setOnClickListener(this);
        binding.btnSearchByDate.setOnClickListener(this);
    }

    public void getRevenueByProduct(int month, int year) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getRevenueByProduct(month, year).enqueue(new Callback<List<ItemProduct>>() {
            @Override
            public void onResponse(Call<List<ItemProduct>> call, Response<List<ItemProduct>> response) {
                if (response.isSuccessful()) {
                    itemProducts = response.body();
                    revenueManagementAdapter = new RevenueManagementAdapter(itemProducts);
                    binding.recycleviewQl.setLayoutManager(new LinearLayoutManager(QuanLyDoanhThuActivity.this));
                    binding.recycleviewQl.setAdapter(revenueManagementAdapter);
                    revenueManagementAdapter.notifyDataSetChanged();
                    revenueManagementAdapter.setOnItemClickListener((RevenueManagementAdapter.OnItemClickListener) QuanLyDoanhThuActivity.this);
                    // Total revenue
                    long revenue = 0;
                    for (ItemProduct i : itemProducts)
                        revenue += i.getTotal();
                    binding.txtdoanhthu.setText(String.valueOf(revenue) + " VNĐ");
                } else
                    Toast.makeText(QuanLyDoanhThuActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<ItemProduct>> call, Throwable t) {
                Toast.makeText(QuanLyDoanhThuActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });

    }

    @Override
    public void itemClick(Order order) {
        Intent intent = new Intent(this, ChiTietDonHangActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onMonthYearSet(int selectedMonth, int selectedYear) {
        month = selectedMonth;
        year = selectedYear;

        binding.pickerMonth.setText(String.valueOf(month));
        binding.pickerYear.setText(String.valueOf(year));
    }

    @Override
    public void onClick(View v) {
        if (v.equals(binding.pickerMonth) || v.equals(binding.pickerYear)) {
            // Filter by date
            pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        }
        if (v.equals(binding.btnSearchByDate)) {
            int month = Integer.parseInt(binding.pickerMonth.getText().toString());
            int year = Integer.parseInt(binding.pickerYear.getText().toString());
            // Lấy tất cả
            getRevenueByProduct(month, year);
        }
    }

    @Override
    public void itemClick(ItemProduct itemProduct) {

    }
}