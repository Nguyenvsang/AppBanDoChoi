package com.example.appbandochoi.adapter;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.appbandochoi.activity.GioHangActivity;
import com.example.appbandochoi.constants.Constants;

import java.io.File;

public class BindingAdapters{
    @BindingAdapter(value = {"android:src", "placeholderImage", "errorImage"}, requireAll = false)
    public static void loadImageWithGlide(ImageView imageView, Object obj, Object placeholder, Object errorImage) {
        RequestOptions options = new RequestOptions();
        if (placeholder instanceof Drawable) options.placeholder((Drawable) placeholder);
        if (placeholder instanceof Integer) options.placeholder((Integer) placeholder);

        if (errorImage instanceof Drawable) options.error((Drawable) errorImage);
        if (errorImage instanceof Integer) options.error((Integer) errorImage);

        RequestManager manager = Glide.with(imageView.getContext()).
                applyDefaultRequestOptions(options);
        RequestBuilder<Drawable> builder;

        if (obj instanceof String) {
            builder = manager.load(Constants.ROOT_URL + (String) obj);
        } else if (obj instanceof Uri) {
            builder = manager.load((Uri) obj);
        } else if (obj instanceof Drawable) {
            builder = manager.load((Drawable) obj);
        } else if (obj instanceof Bitmap) {
            builder = manager.load((Bitmap) obj);
        } else if (obj instanceof Integer) {
            builder = manager.load((Integer) obj);
        } else if (obj instanceof File) {
            builder = manager.load((File) obj);
        } else if (obj instanceof Byte[]) {
            builder = manager.load((Byte[]) obj);
        } else{
            builder = manager.load(obj);
        }
        builder.into(imageView);
    }

    @BindingAdapter("android:bitmapSrc")
    public static void setBitmapSrc(ImageView imageView, Bitmap bitmap) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(bitmap)
                .into(imageView);
    }
}
