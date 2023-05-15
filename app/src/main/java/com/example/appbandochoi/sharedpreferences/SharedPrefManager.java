package com.example.appbandochoi.sharedpreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.appbandochoi.activity.DangNhapActivity;
import com.example.appbandochoi.model.User;
import com.example.appbandochoi.utils.DateUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SharedPrefManager {
    // Khởi tạo các hằng key
    private static final String SHARED_PREF_NAME = "UserLogin";
    private static final String KEY_USERID = "keyuserid";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_BIRTHDAY = "keybirthday";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_ADDRESS = "keyaddress";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_IMAGE = "keyimage";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_ROLE = "keyrole";
    private static SharedPrefManager mInstance;
    private static Context ctx;
    // Khởi tạo constructor
    private SharedPrefManager(Context context){
        ctx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    // Lưu thông tin người dùng
    public void userLogin(User user){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USERID, user.getUserID());
        editor.putString(KEY_FIRSTNAME, user.getFirstname());
        editor.putString(KEY_LASTNAME, user.getLastname());
        editor.putInt(KEY_GENDER, user.getGender());
        editor.putString(KEY_BIRTHDAY, user.getBirthDay() == null ? null : user.getBirthDay().toString());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_IMAGE, user.getImage());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putBoolean(KEY_ROLE, user.isRole());
        editor.apply();
    }
    // Kiểm tra người dùng đã đăng nhập hay chưa
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }
    // Trả về người dùng đã đăng nhập
    public User getUser() throws ParseException {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Timestamp birthDay = null;
        String s = sharedPreferences.getString(KEY_BIRTHDAY, null);
        if (s != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
            birthDay = new Timestamp(sdf.parse(s).getTime());
        }
        return new User(
                sharedPreferences.getInt(KEY_USERID, -1),
                sharedPreferences.getString(KEY_FIRSTNAME, null),
                sharedPreferences.getString(KEY_LASTNAME, null),
                sharedPreferences.getInt(KEY_GENDER, -1),
                birthDay,
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_ADDRESS, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_IMAGE, null),
                sharedPreferences.getBoolean(KEY_ROLE, true),
                true,
                sharedPreferences.getString(KEY_USERNAME, null),
                null,
                null
        );
    }
    // Đăng xuất người dùng
    public void logOut(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, DangNhapActivity.class));
    }
}
