package com.example.appbandochoi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbandochoi.R;
import com.example.appbandochoi.asynctask.ConvertImageFromURLTask;
import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.databinding.ItemQuanLySanPhamBinding;
import com.example.appbandochoi.databinding.ItemSanphamBinding;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.ImageConverter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.MyViewHolder> {
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;
    private APIService apiService;
    private Context context;
    private Product product;

    public ProductManagementAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductManagementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuanLySanPhamBinding itemQuanLySanPhamBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_quan_ly_san_pham, parent, false);
        return new MyViewHolder(itemQuanLySanPhamBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductManagementAdapter.MyViewHolder holder, int position) {
        holder.setBinding(productList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (productList == null)
            return 0;
        return productList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> productName = new ObservableField<>();
        public ObservableField<String> price = new ObservableField<>();
        public ObservableField<String> quantity = new ObservableField<>();
        public ObservableField<Bitmap> images = new ObservableField<>();
        public ObservableField<String> description = new ObservableField<>();
        public ItemQuanLySanPhamBinding itemQuanLySanPhamBinding;
        private OnItemClickListener onItemClickListener;
        private Product product;
        private Button btnCapNhatSoLuongSanPham;
        private RadioGroup rdGroupProductStatus;
        private RadioButton rdBan, rdNgungBan;
        private EditText edtCapNhatSoLuong;

        public MyViewHolder(ItemQuanLySanPhamBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemQuanLySanPhamBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);

            edtCapNhatSoLuong = itemView.getRoot().findViewById(R.id.edtCapNhatSoLuong);
            btnCapNhatSoLuongSanPham = itemView.getRoot().findViewById(R.id.btnCapNhatSoLuongSanPham);
            rdGroupProductStatus = itemView.getRoot().findViewById(R.id.rdGroupProductStatus);
            rdBan = itemView.getRoot().findViewById(R.id.rdBan);
            rdNgungBan = itemView.getRoot().findViewById(R.id.rdNgungBan);
        }

        public void setBinding(Product product, int position) {
            if (itemQuanLySanPhamBinding.getViewHolder() == null) {
                itemQuanLySanPhamBinding.setViewHolder(this);
            }
            this.product = product;
            productName.set(product.getProductName());
            price.set(String.valueOf(product.getPrice()));
            edtCapNhatSoLuong.setText(product.getQuantity() == 0 ? "Hết hàng" : String.valueOf(product.getQuantity()));

            ConvertImageFromURLTask task = new ConvertImageFromURLTask(images);
            task.execute(Constants.ROOT_URL + product.getImages());

            description.set(product.getDescription());

            if (product.isStatus())
                rdBan.setChecked(true);
            else
                rdNgungBan.setChecked(true);

            // Click event
            rdGroupProductStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.rdBan) {
                        product.setStatus(true);
                        updateStatus(product.getProductID(), true, position);
                    } else if(checkedId == R.id.rdNgungBan) {
                        product.setStatus(false);
                        updateStatus(product.getProductID(), false, position);
                    }
                }
            });

            btnCapNhatSoLuongSanPham.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int updatedQuantity = Integer.parseInt(edtCapNhatSoLuong.getText().toString());
                    product.setQuantity(updatedQuantity);  // Update the quantity in the product object
                    notifyItemChanged(position, product);  // Refresh the corresponding item in the RecyclerView
                    updateQuantity(product.getProductID(), updatedQuantity, position);
                }
            });

//            rdBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        rdBan.setChecked(true);
//                        updateStatus(product.getProductID(), true, position);
//                    }
//                }
//            });
//
//            rdNgungBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        rdNgungBan.setChecked(true);
//                        updateStatus(product.getProductID(), false, position);
//                    }
//                }
//            });
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(product);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Product product);
    }

    public void updateStatus(int productID, boolean status, int position) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateStatus(productID, status).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    product = response.body();
                    Toast.makeText(context, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    productList.set(position, product);
                } else {
                    int statusCode = response.code();
                    Toast.makeText(context, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(context, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateQuantity(int productID, int quantity, int position) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateQuantity(productID, quantity).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    product = response.body();
                    Toast.makeText(context, "Cập nhật số lượng thành công!", Toast.LENGTH_SHORT).show();
                    // Update the quantity in the productList
                    productList.set(position, product);
                } else {
                    int statusCode = response.code();
                    Toast.makeText(context, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(context, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
