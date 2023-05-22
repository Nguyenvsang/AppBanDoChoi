package com.example.appbandochoi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.activity.DanhGiaActivity;
import com.example.appbandochoi.activity.XemDonActivity;
import com.example.appbandochoi.databinding.ItemDonhangBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<Order> orderList;
    private OrderAdapter.OnItemClickListener onItemClickListener;
    private APIService apiService;
    private Context context;
    Button btnthanhtoanlai, btnhuybodon;
    LinearLayout linearLayoutDonhang;
    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonhangBinding itemDonhangBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_donhang, parent, false);
        return new OrderAdapter.MyViewHolder(itemDonhangBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        holder.setBinding(orderList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (orderList == null)
            return 0;
        return orderList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> orderID = new ObservableField<>();
        public ObservableField<String> orderedDate = new ObservableField<>();
        public ObservableField<String> receivedDate = new ObservableField<>();
        public ObservableField<String> cancelledDate = new ObservableField<>();
        public ObservableField<String> total = new ObservableField<>();
        public ObservableField<String> receiverName = new ObservableField<>();
        public ObservableField<String> address = new ObservableField<>();
        public ObservableField<String> phone = new ObservableField<>();
        public ItemDonhangBinding itemDonhangBinding;
        private OnItemClickListener onItemClickListener;
        private Order order;

        public MyViewHolder(ItemDonhangBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemDonhangBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);

            // Initialize component
            btnthanhtoanlai = itemView.getRoot().findViewById(R.id.btnthanhtoanlai);
            btnhuybodon = itemView.getRoot().findViewById(R.id.btnhuybodon);
            linearLayoutDonhang = itemView.getRoot().findViewById(R.id.linearLayoutDonhang);
        }

        public void setBinding(Order order, int position) {
            if (itemDonhangBinding.getViewHolder() == null) {
                itemDonhangBinding.setViewHolder(this);
            }

            this.order = order;
            String status = "";
            switch (order.getStatus()) {
                case 0:
                    status = "Đang chờ thanh toán";
                    break;
                case 1:
                    status = "Đang chờ xác nhận";
                    break;
                case 2:
                    status = "Đã xác nhận. Đang chờ vận chuyển.";
                    break;
                case 3:
                    status = "Đang vận chuyển";
                    break;
                case 4:
                    status = "Đã nhận";
                    break;
                default:
                    status = "Đã huỷ";
                    break;
            }
            orderID.set("Đơn hàng: " + String.valueOf(order.getOrderID()) + " | " + status);
            orderedDate.set(new ShortDateUtil().parseShortDate(order.getOrderedDate()));
            if (order.getReceivedDate() == null)
                receivedDate.set("");
            else
                receivedDate.set(new ShortDateUtil().parseShortDate(order.getReceivedDate()));
            if (order.getCancelledDate() == null)
                cancelledDate.set("");
            else
                cancelledDate.set(new ShortDateUtil().parseShortDate(order.getCancelledDate()));
            receiverName.set(order.getReceiverName());
            phone.set(order.getPhone());
            address.set(order.getAddress());
            total.set(String.valueOf(order.getTotal()));
            // Set button visibility
            if (order.getStatus() == 0) {
                btnhuybodon.setVisibility(View.VISIBLE);
                btnthanhtoanlai.setVisibility(View.VISIBLE);
                linearLayoutDonhang.setVisibility(View.VISIBLE);
            } else if (order.getStatus() == 1 || order.getStatus() == 2) {
                btnhuybodon.setVisibility(View.VISIBLE);
                btnthanhtoanlai.setVisibility(View.GONE);
                linearLayoutDonhang.setVisibility(View.VISIBLE);
            } else {
                btnhuybodon.setVisibility(View.GONE);
                btnthanhtoanlai.setVisibility(View.GONE);
                linearLayoutDonhang.setVisibility(View.GONE);
            }
            // Click event
            btnthanhtoanlai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderStatus(order.getOrderID(), 1, position);
                    Toast.makeText(context, "Thanh toán thành công! Cảm ơn quý khách!", Toast.LENGTH_SHORT).show();
                }
            });

            btnhuybodon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có chắc chắn muốn huỷ đơn hàng này?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateOrderStatus(order.getOrderID(), 5, position);
                            Toast.makeText(context, "Đã huỷ đơn hàng. Mong được phục vụ quý khách lần sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Huỷ bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(order);
        }
    }

    public void setOnItemClickListener(OrderAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Order order);
    }

    public void updateOrderStatus(int orderID, int status, int position) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateOrderStatus(orderID, status).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Order order = response.body();
                    orderList.set(position, order);
                    notifyItemChanged(position);
                }
                else
                    Toast.makeText(context, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(context, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }
}
