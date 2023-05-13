package com.example.appbandochoi.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.appbandochoi.R;
import com.example.appbandochoi.activity.HomeActivity;

public class GeneralFunction {
    private Context context;
    public void clickActtivate (LinearLayout linearTrangchu, LinearLayout linearSanpham, LinearLayout linearDonhang,
                                LinearLayout linearTaikhoan, Button btnCart) {

        linearTrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.finish();
            }
        });

        linearSanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        linearDonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        linearTaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
