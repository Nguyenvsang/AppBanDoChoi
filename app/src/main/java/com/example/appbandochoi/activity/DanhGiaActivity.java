package com.example.appbandochoi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appbandochoi.R;
import com.example.appbandochoi.model.FullOrderItem;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.RealPathUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhGiaActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnChoose, btnSubmit;
    ImageView imgBack, imgReview;
    RatingBar ratingBar;
    EditText editTextComment;
    private Uri mUri;
    private User storedUser;
    private FullOrderItem orderItem;
    private APIService apiService;
    private ProgressDialog mProgressDialog;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = DanhGiaActivity.class.getName();
    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    private void anhXa() {
        btnChoose = findViewById(R.id.btnUploadReviewImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgBack = findViewById(R.id.imgBack);
        imgReview = findViewById(R.id.imgReview);
        ratingBar = findViewById(R.id.ratingBar);
        editTextComment = findViewById(R.id.editTextComment);
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    // Get image from Gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đi :P"));
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //request code
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgReview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void review() {
        mProgressDialog.show();
        // Check logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                storedUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                startActivity(new Intent(DanhGiaActivity.this, DangNhapActivity.class));
                finish();
            }
        }
        // Get order item
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        orderItem = (FullOrderItem) bundle.getSerializable("orderItem");
        // Validate
        if (ratingBar.getRating() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rating bar")
                    .setMessage("Vui lòng chọn mức độ hài lòng.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        // Create RequestBody from int
        RequestBody orderItemID = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(orderItem.getOrderItemID()));
        RequestBody star = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingBar.getRating()));
        RequestBody comment = RequestBody.create(MediaType.parse("multipart/form-data"), editTextComment.getText().toString());
        System.out.println(orderItem.getOrderItemID());
        System.out.println(ratingBar.getRating());
        System.out.println(editTextComment.getText().toString());
        System.out.println(mUri);
        // create RequestBody instance from file
        String IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
        Log.e("Image path: ", IMAGE_PATH);
        File file = new File(IMAGE_PATH);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file); // MultipartBody. Part is used to send also the actual file name
        MultipartBody.Part partProfileImage =
                MultipartBody.Part.createFormData("images", file.getName(), requestFile);
        //gọi Retrofit

        APIService.apiService.reviewOrderItem(orderItemID, star, comment, partProfileImage).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                mProgressDialog.dismiss();
                Review review = response.body();
                if (review == null) {
                    Toast.makeText(DanhGiaActivity.this, "Đánh giá thất bại", Toast.LENGTH_SHORT).show();
                } else if (review != null) {
                    Toast.makeText(DanhGiaActivity.this, "Thành công", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DanhGiaActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("TAG", t.toString());
                Toast.makeText(DanhGiaActivity.this, "Gọi API thất bại", Toast.LENGTH_LONG).show();
                System.out.println(t);
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_gia);

        // Mapping
        anhXa();
        // Action
        imgBack.setOnClickListener(this);

        //khởi tạo Progressbar
        mProgressDialog = new ProgressDialog(DanhGiaActivity.this); //bắt sự kiện nút chọn ảnh
        mProgressDialog.setMessage("Đang tải ảnh lên, vui lòng đợi...");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(); //checks quyền
            }
        });

        //bắt sự kiện upload ảnh
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri != null) {
                    review();
                    // Get order by order item from review
                    apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    apiService.getOrderByOrderItem(orderItem.getOrderItemID()).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            if (response.isSuccessful()) {
                                Order order = response.body();

                                Intent intent = new Intent(getApplicationContext(), ChiTietDonHangActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("order", order);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Toast.makeText(DanhGiaActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            Toast.makeText(DanhGiaActivity.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(imgBack)) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }
}