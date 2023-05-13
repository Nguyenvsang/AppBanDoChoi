package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.adapter.OrderAdapter;
import com.example.appbandochoi.databinding.ActivityQuanLyDonHangBinding;
import com.example.appbandochoi.databinding.ActivityXemDonBinding;
import com.example.appbandochoi.fragment.MonthYearPickerDialog;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuanLyDonHangActivity extends AppCompatActivity
        implements OrderAdapter.OnItemClickListener, DatePickerDialog.OnDateSetListener, MonthYearPickerDialog.OnMonthYearSetListener, View.OnClickListener {

    private APIService apiService;
    private ActivityQuanLyDonHangBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Context context;
    private AutoCompleteTextView autoCompleteFilter;
    private int month, year;
    MonthYearPickerDialog pd = new MonthYearPickerDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        pd.setListener(this, this, this);

        orderAdapter = new OrderAdapter(orderList, context);
        getOrderDesc();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quan_ly_don_hang);
        binding.setOrder(this);

        // Filter options
        String[] sorts = getResources().getStringArray(R.array.filter);
        // Create an array adapter and pass the required parameters
        // In our case, pass the context, drop-down layout, and array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_sort, sorts);
        // Get reference to the AutoCompleteTextView
        autoCompleteFilter = binding.autoCompleteFilter;
        // Set the adapter to the AutoCompleteTextView
        autoCompleteFilter.setAdapter(arrayAdapter);

        //Click
        autoCompleteFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Have filtered by Date
                if (binding.pickerMonth.getText().toString() == "0" || binding.pickerYear.getText().toString() == "0") {
                    // Compare the selected item with each item to determine which one was clicked
                    if (selectedItem.equals("Chờ thanh toán")) {
                        getOrderByStatus(1);
                    } else if (selectedItem.equals("Đang vận chuyển")) {
                        getOrderByStatus(2);
                    } else if (selectedItem.equals("Đã nhận hàng")) {
                        getOrderByStatus(3);
                    } else if (selectedItem.equals("Đã huỷ")) {
                        getOrderByStatus(4);
                    } else {
                        getOrderByStatus(0);
                    }
                } else {// In constrast
                    int month = Integer.parseInt(binding.pickerMonth.getText().toString());
                    int year = Integer.parseInt(binding.pickerYear.getText().toString());
                    if (selectedItem.equals("Chờ thanh toán")) {
                        getOrderByStatusAndDate(1, month, year);
                    } else if (selectedItem.equals("Đang vận chuyển")) {
                        getOrderByStatusAndDate(2, month, year);
                    } else if (selectedItem.equals("Đã nhận hàng")) {
                        getOrderByStatusAndDate(3, month, year);
                    } else if (selectedItem.equals("Đã huỷ")) {
                        getOrderByStatusAndDate(4, month, year);
                    } else {
                        getOrderByStatusAndDate(0, month, year);
                    }
                }
            }
        });

        binding.pickerMonth.setText(String.valueOf(month));
        binding.pickerYear.setText(String.valueOf(year));

        binding.pickerMonth.setOnClickListener(this);
        binding.pickerYear.setOnClickListener(this);
        binding.btnSearchByDate.setOnClickListener(this);
    }

    public void getOrderDesc() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderDesc().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(orderList, context);
                    binding.recycleviewQl.setLayoutManager(new LinearLayoutManager(QuanLyDonHangActivity.this));
                    binding.recycleviewQl.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                    orderAdapter.setOnItemClickListener((OrderAdapter.OnItemClickListener) QuanLyDonHangActivity.this);
                } else
                    Toast.makeText(QuanLyDonHangActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(QuanLyDonHangActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getOrderByStatus(int status) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderByStatus(status).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(orderList, context);
                    binding.recycleviewQl.setLayoutManager(new LinearLayoutManager(QuanLyDonHangActivity.this));
                    binding.recycleviewQl.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                    orderAdapter.setOnItemClickListener((OrderAdapter.OnItemClickListener) QuanLyDonHangActivity.this);
                } else
                    Toast.makeText(QuanLyDonHangActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(QuanLyDonHangActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void getOrderByStatusAndDate(int status, int month, int year) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getOrderByStatusAndDate(status, month, year).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(orderList, context);
                    binding.recycleviewQl.setLayoutManager(new LinearLayoutManager(QuanLyDonHangActivity.this));
                    binding.recycleviewQl.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                    orderAdapter.setOnItemClickListener((OrderAdapter.OnItemClickListener) QuanLyDonHangActivity.this);
                } else
                    Toast.makeText(QuanLyDonHangActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(QuanLyDonHangActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
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
            getOrderByStatusAndDate(0, month, year);
        }
    }
}