package com.example.appbandochoi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbandochoi.R;
import com.example.appbandochoi.databinding.ItemDanhmucBinding;
import com.example.appbandochoi.databinding.ItemSanphamBinding;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.retrofit2.APIService;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<Category> categoryList;
    private CategoryAdapter.OnItemClickListener onItemClickListener;
    private APIService apiService;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDanhmucBinding itemDanhmucBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_danhmuc, parent, false);
        return new CategoryAdapter.MyViewHolder(itemDanhmucBinding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        holder.setBinding(categoryList.get(position), position);
    }


    @Override
    public int getItemCount() {
        if (categoryList == null)
            return 0;
        return categoryList.size();
    }

    // Tạo class MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ObservableField<String> categoryName = new ObservableField<>();
        public ObservableField<String> image = new ObservableField<>();
        public ItemDanhmucBinding itemDanhmucBinding;
        private CategoryAdapter.OnItemClickListener onItemClickListener;
        private Category category;

        public MyViewHolder(ItemDanhmucBinding itemView, CategoryAdapter.OnItemClickListener onItemClickListener) {
            super(itemView.getRoot());
            this.itemDanhmucBinding = itemView;
            this.onItemClickListener = onItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void setBinding(Category category, int position) {
            if (itemDanhmucBinding.getViewHolder() == null) {
                itemDanhmucBinding.setViewHolder(this);
            }

            this.category = category;
            categoryName.set(category.getCategoryName());
            image.set(category.getImage());
        }

        public void onClick(View v) {
            this.onItemClickListener.itemClick(category);
        }
    }

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void itemClick(Category category);
    }
}