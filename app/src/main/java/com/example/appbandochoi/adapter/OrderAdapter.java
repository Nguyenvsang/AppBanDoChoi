package com.example.appbandochoi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.databinding.ItemDonhangBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.utils.ShortDateUtil;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<Order> orderList;
    private OrderAdapter.OnItemClickListener onItemClickListener;
    private APIService apiService;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    Button btnthanhtoanlai, btnhuybodon;

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

            // Initialize button
            btnthanhtoanlai = itemView.getRoot().findViewById(R.id.btnthanhtoanlai);
            btnhuybodon = itemView.getRoot().findViewById(R.id.btnhuybodon);
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
                    status = "Đang vận chuyển";
                    break;
                case 2:
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
            if (order.getStatus() != 0) {
                btnhuybodon.setVisibility(View.GONE);
                btnthanhtoanlai.setVisibility(View.GONE);
            }
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
}
