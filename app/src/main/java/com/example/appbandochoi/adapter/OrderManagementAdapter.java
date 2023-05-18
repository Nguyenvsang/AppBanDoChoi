package com.example.appbandochoi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.example.appbandochoi.activity.DangNhapActivity;
import com.example.appbandochoi.activity.ProfileActivity;
import com.example.appbandochoi.activity.XemDonActivity;
import com.example.appbandochoi.databinding.ItemDonhangBinding;
import com.example.appbandochoi.databinding.ItemQuanLyDonHangBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.MyViewHolder> {
    private List<Order> orderList;
    private OrderManagementAdapter.OnItemClickListener onItemClickListener;
    private APIService apiService;
    private Context context;

    public OrderManagementAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderManagementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuanLyDonHangBinding itemQuanLyDonHangBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_quan_ly_don_hang, parent, false);
        return new OrderManagementAdapter.MyViewHolder(itemQuanLyDonHangBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManagementAdapter.MyViewHolder holder, int position) {
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
        public ItemQuanLyDonHangBinding itemQuanLyDonHangBinding;
        private OnItemClickListener onItemClickListener;
        private Order order;

        public MyViewHolder(ItemQuanLyDonHangBinding itemView, OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemQuanLyDonHangBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(Order order, int position) {
            if (itemQuanLyDonHangBinding.getViewHolder() == null) {
                itemQuanLyDonHangBinding.setViewHolder(this);
            }

            // Initialize component
            Button btnxacnhan = itemQuanLyDonHangBinding.getRoot().findViewById(R.id.btnxacnhan);
            Button btnguihang = itemQuanLyDonHangBinding.getRoot().findViewById(R.id.btnguihang);
            Button btngiaohang = itemQuanLyDonHangBinding.getRoot().findViewById(R.id.btngiaohang);
            Button btnhuybodon = itemQuanLyDonHangBinding.getRoot().findViewById(R.id.btnhuybodon);

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

            // Click event
            btnxacnhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Xác nhận đơn hàng này?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateOrderStatus(order.getOrderID(), 2, position);
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
            btnguihang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Giao đơn hàng này cho bên vận chuyển?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateOrderStatus(order.getOrderID(), 3, position);
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
            btngiaohang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Giao hàng và hoàn thành đơn hàng này?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateOrderStatus(order.getOrderID(), 4, position);
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

            // Set button activeness
            if (order.getStatus() == 0) {
                btnxacnhan.setEnabled(false);
                btnxacnhan.setAlpha(0.5f);
                btnguihang.setEnabled(false);
                btnguihang.setAlpha(0.5f);
                btngiaohang.setEnabled(false);
                btngiaohang.setAlpha(0.5f);
                btnhuybodon.setEnabled(true);
                btnhuybodon.setAlpha(1.0f);
            } else if (order.getStatus() == 1) {
                btnxacnhan.setEnabled(true);
                btnxacnhan.setAlpha(1.0f);
                btnguihang.setEnabled(false);
                btnguihang.setAlpha(0.5f);
                btngiaohang.setEnabled(false);
                btngiaohang.setAlpha(0.5f);
                btnhuybodon.setEnabled(true);
                btnhuybodon.setAlpha(1.0f);
            } else if (order.getStatus() == 2) {
                btnxacnhan.setEnabled(false);
                btnxacnhan.setAlpha(0.5f);
                btnguihang.setEnabled(true);
                btnguihang.setAlpha(1.0f);
                btngiaohang.setEnabled(false);
                btngiaohang.setAlpha(0.5f);
                btnhuybodon.setEnabled(true);
                btnhuybodon.setAlpha(1.0f);
            } else if (order.getStatus() == 3) {
                btnxacnhan.setEnabled(false);
                btnxacnhan.setAlpha(0.5f);
                btnguihang.setEnabled(false);
                btnguihang.setAlpha(0.5f);
                btngiaohang.setEnabled(true);
                btngiaohang.setAlpha(1.0f);
                btnhuybodon.setEnabled(false);
                btnhuybodon.setAlpha(0.5f);
            } else if (order.getStatus() == 4 || order.getStatus() == 5){
                Log.d("OrderManagementAdapter", "Order ID: " + order.getOrderID() + ", Status: " + order.getStatus());
                btnxacnhan.setEnabled(false);
                btnxacnhan.setAlpha(0.5f);
                btnguihang.setEnabled(false);
                btnguihang.setAlpha(0.5f);
                btngiaohang.setEnabled(false);
                btngiaohang.setAlpha(0.5f);
                btnhuybodon.setEnabled(false);
                btnhuybodon.setAlpha(0.5f);
                System.out.println(order.getOrderID() + " " + order.getStatus());
            }
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(order);
        }
    }

    public void setOnItemClickListener(OrderManagementAdapter.OnItemClickListener onItemClickListener) {
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
                } else
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
