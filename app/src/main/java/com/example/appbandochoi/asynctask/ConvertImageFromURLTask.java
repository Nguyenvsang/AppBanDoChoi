package com.example.appbandochoi.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.databinding.ObservableField;

import com.example.appbandochoi.utils.ImageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConvertImageFromURLTask extends AsyncTask<String, Void, Bitmap> {

    public ObservableField<Bitmap> images = new ObservableField<>();

    public ConvertImageFromURLTask(ObservableField<Bitmap> images) {
        this.images = images;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap bitmap = loadImageFromUrl(imageUrl);
        if (bitmap != null) {
            Bitmap convertedBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 30);
            return convertedBitmap;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        images.set(result);
    }

    private Bitmap loadImageFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
