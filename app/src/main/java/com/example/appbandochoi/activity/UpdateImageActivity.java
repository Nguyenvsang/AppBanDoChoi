package com.example.appbandochoi.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.RealPathUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateImageActivity extends AppCompatActivity {

    Button btnChoose, btnUpload;
    ImageView imageViewChoose;
    private Uri mUri;
    private User storedUser;
    private ProgressDialog mProgressDialog;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = UpdateImageActivity.class.getName();
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

    private void AnhXa() {
        btnChoose = findViewById(R.id.btnChooseImage);
        btnUpload = findViewById(R.id.btnUpdateImage);
        imageViewChoose = findViewById(R.id.imageProfileUpdate);
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
                            imageViewChoose.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void UploadImage() {
        mProgressDialog.show();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                storedUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                startActivity(new Intent(UpdateImageActivity.this, DangNhapActivity.class));
                finish();
            }
        }
        // Create RequestBody from int
        RequestBody requestUserID = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(storedUser.getUserID()));
        // create RequestBody instance from file
        String IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
        Log.e("Image path: ", IMAGE_PATH);
        File file = new File(IMAGE_PATH);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file); // MultipartBody. Part is used to send also the actual file name
        MultipartBody.Part partProfileImage =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        //gọi Retrofit
        APIService.apiService.updateProfileImage(requestUserID, partProfileImage).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressDialog.dismiss();
                User user = response.body();
                if (user == null) {
                    Toast.makeText(UpdateImageActivity.this, "Cập nhật ảnh bị lỗi", Toast.LENGTH_SHORT).show();
                } else if (user != null) {
                    Toast.makeText(UpdateImageActivity.this, "Thành công", Toast.LENGTH_LONG).show();
                    // Pass data to Profile
                    Intent intent = new Intent(UpdateImageActivity.this, ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(UpdateImageActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("TAG", t.toString());
                Toast.makeText(UpdateImageActivity.this, "Gọi API thất bại", Toast.LENGTH_LONG).show();
                System.out.println(t);
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_image);
        //ánh xạ
        AnhXa();
        //khởi tạo Progressbar
        mProgressDialog = new ProgressDialog(UpdateImageActivity.this); //bắt sự kiện nút chọn ảnh
        mProgressDialog.setMessage("Đang tải ảnh lên, vui lòng đợi...");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(); //checks quyền
            }
        });

        //bắt sự kiện upload ảnh
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri != null) {
                    UploadImage();
                }
            }
        });
    }
}