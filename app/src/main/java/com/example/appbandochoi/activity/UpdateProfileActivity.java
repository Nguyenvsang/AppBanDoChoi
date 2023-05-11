package com.example.appbandochoi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.appbandochoi.R;
import com.example.appbandochoi.databinding.ActivityThanhToanBinding;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.retrofit2.APIService;
import com.example.appbandochoi.retrofit2.RetrofitClient;
import com.example.appbandochoi.sharedpreferences.SharedPrefManager;
import com.example.appbandochoi.utils.DateUtil;
import com.example.appbandochoi.utils.ShortDateUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private User user, storedUser;
    private APIService apiService;
    private EditText editTextLastname, editTextFirstname, editTextAddress, editTextPhone, editTextBirthDay;
    private RadioButton radioButtonMale, radioButtonFemale, radioButtonOther;
    private RadioGroup radioGroupGender;
    private Button btnUpdate, btnDatePicker;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        anhXa();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            storedUser = null;
            try {
                storedUser = SharedPrefManager.getInstance(this).getUser();
            } catch (ParseException e) {
                startActivity(new Intent(UpdateProfileActivity.this, DangNhapActivity.class));
                finish();
            }

            user = getUser(storedUser.getUserID());

        } else {
            startActivity(new Intent(UpdateProfileActivity.this, DangNhapActivity.class));
            finish();
        }

        anhXa();

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        btnDatePicker.setOnClickListener(UpdateProfileActivity.this);
        btnUpdate.setOnClickListener(UpdateProfileActivity.this);
        radioGroupGender.setOnCheckedChangeListener(UpdateProfileActivity.this);
    }

    public void anhXa() {
        editTextFirstname = findViewById(R.id.editTextFirstnameUp);
        editTextLastname = findViewById(R.id.editTextLastnameUp);
        editTextBirthDay = findViewById(R.id.editTextBirthdayUp);
        editTextAddress = findViewById(R.id.editTextAddressUp);
        editTextPhone = findViewById(R.id.editTextPhoneUp);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioButtonOther = findViewById(R.id.radioButtonOther);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDatePicker = findViewById(R.id.btnDatePicker);
    }

    public User getUser(int userID) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUser(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    displayUser();

                } else {
                    int statusCode = response.code();
                    Toast.makeText(UpdateProfileActivity.this, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(UpdateProfileActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        return user;
    }

    public void displayUser() {
        editTextFirstname.setText(user.getFirstname());
        editTextLastname.setText(user.getLastname());
        if (user.getGender() == 0)
            radioButtonMale.setChecked(true);
        else if (user.getGender() == 1)
            radioButtonFemale.setChecked(true);
        else
            radioButtonOther.setChecked(true);
        if (user.getBirthDay() != null)
            editTextBirthDay.setText(new ShortDateUtil().parseShortDate(user.getBirthDay()));
        else
            editTextBirthDay.setText("");
        editTextAddress.setText(user.getAddress());
        editTextPhone.setText(user.getPhone());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        String selectedDate = dateFormatter.format(calendar.getTime());
                        editTextBirthDay.setText(selectedDate);
                    }
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    public void updateProfile() {
        // Lấy giá trị
        final String firstname = editTextFirstname.getText().toString().trim();
        final String lastname = editTextLastname.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        String birthDay = editTextBirthDay.getText().toString().trim();
        int gender;
        if (radioButtonMale.isChecked())
            gender = 0;
        else if (radioButtonFemale.isChecked())
            gender = 1;
        else
            gender = 2;

        // Convert date
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "yyyy-MM-dd";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

        try {
            Date date = inputFormat.parse(birthDay);
            birthDay = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Check input
        if (TextUtils.isEmpty(firstname)) {
            editTextFirstname.setError("Vui lòng nhập tên!");
            editTextFirstname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Vui lòng nhập số điện thoại!");
            editTextPhone.requestFocus();
            return;
        }
        if (phone.length() != 10) {
            editTextPhone.setError("Vui lòng nhập số điện thoại 10 số!");
            editTextPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError("Vui lòng nhập địa chỉ!");
            editTextAddress.requestFocus();
            return;
        }
        Map<String, Object> userModel = new HashMap<>();
        userModel.put("userID", 11);
        userModel.put("lastname", lastname);
        userModel.put("firstname", firstname);
        userModel.put("gender", gender);
        userModel.put("birthDay", birthDay);
        userModel.put("phone", phone);
        userModel.put("address", address);

        Gson gson = new Gson();
        String json = gson.toJson(userModel);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateProfile(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    JSONObject updatedUser;
                    try {
                        String responseBody = response.body().string();
                        updatedUser = new JSONObject(responseBody);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        if (updatedUser.getString("message").equals("Username has been already taken.")) {
                            Toast.makeText(UpdateProfileActivity.this, "Tên người dùng đã được đăng ký!", Toast.LENGTH_SHORT).show();
                        } else if (updatedUser.getString("message").equals("Email has been already registered.")) {
                            Toast.makeText(UpdateProfileActivity.this, "Email đã được đăng ký!", Toast.LENGTH_SHORT).show();
                        } else if (updatedUser.getString("message").equals("Update profile successfully!")) {
                            updatedUser = updatedUser.getJSONObject("data");
                            // Convert
                            Gson gson = new Gson();
                            user = gson.fromJson(updatedUser.toString(), User.class);
                            // Update
                            displayUser();

                            // Toast message
                            Toast.makeText(UpdateProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                            // Pass data to Profile
                            Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println(response.errorBody().toString() +"          " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
                Toast.makeText(UpdateProfileActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        if (view.equals(btnUpdate)) {
            updateProfile();
        } else if (view.equals(btnDatePicker)) {
            showDatePicker();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // Uncheck all other RadioButtons in the RadioGroup
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            if (radioButton.getId() != checkedId) {
                radioButton.setChecked(false);
            }
        }
    }
}