package com.example.appbandochoi.retrofit2;

import com.example.appbandochoi.constants.Constants;
import com.example.appbandochoi.model.Category;
import com.example.appbandochoi.model.FullOrderItem;
import com.example.appbandochoi.model.Image;
import com.example.appbandochoi.model.ItemProduct;
import com.example.appbandochoi.model.Order;
import com.example.appbandochoi.model.OrderItem;
import com.example.appbandochoi.model.Product;
import com.example.appbandochoi.model.Review;
import com.example.appbandochoi.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    APIService apiService = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(APIService.class);

    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/signup")
    Call<ResponseBody> signup(@Field("username") String username, @Field("password") String password,
                              @Field("firstname") String firstname, @Field("lastname") String lastname,
                              @Field("email") String email, @Field("phone") String phone);
    @FormUrlEncoded
    @POST("cart/mycart")
    Call<ResponseBody> displayCart(@Field("userID") int userID);

    @PUT("cart/update")
    Call<ResponseBody> updateCart(@Body RequestBody cartItemModel);

    @DELETE("cart/delete")
    Call<ResponseBody> deleteCartItem(@Query("cartItemID") int cartItemID);

    @GET("user/me/{userID}")
    Call<User> getUser(@Path("userID") int userID);

    @POST("order/place/{userID}")
    Call<Order> placeOrder(@Path("userID") int userID, @Body RequestBody orderModel);

    @GET("product/all")
    Call<List<Product>> getProductList();

    @GET("product/forsale")
    Call<List<Product>> getProductListForSale();

    @GET("product/image")
    Call<List<Image>> getImagesForProduct(@Query("productID") int productID);

    @GET("product/forsale/sort")
    Call<List<Product>> getProductAndSort(@Query("sort") int sort);

    @GET("product/forsale/category")
    Call<List<Product>> getProductByCategory(@Query("categoryID") int categoryID);

    @GET("product/forsale/category/sort")
    Call<List<Product>> getProductByCategoryAndSort(@Query("categoryID") int categoryID, @Query("sort") int sort);

    @GET("product/search")
    Call<List<Product>> searchProduct(@Query("q") String searchString);

    @GET("product/mostpopular")
    Call<List<Product>> getMostPopular();

    @GET("product/search/sort")
    Call<List<Product>> searchProductAndSort(@Query("q") String searchString, @Query("sort") int sort);

    @FormUrlEncoded
    @POST("review")
    Call<List<Review>> getReviewListByProduct(@Field("productID") int productID);

    @GET("user/review")
    Call<User> getUserByReview(@Query("reviewID") int reviewID);

    @GET("orderitem/review")
    Call<OrderItem> getOrderItemByReview(@Query("reviewID") int reviewID);

    @FormUrlEncoded
    @POST("cart/add")
    Call<ResponseBody> addToCart(@Field("productID") int productID, @Field("quantity") int quantity, @Field("userID") int userID);

    @FormUrlEncoded
    @POST("order/my")
    Call<List<Order>> getMyOrder(@Field("userID") int userID);

    @GET("review/orderitem")
    Call<Review> getReviewByOrderItem(@Query("orderItemID") int orderItemID);

    @GET("product/orderitem")
    Call<Product> getProductByOrderItem(@Query("orderItemID") int orderItemID);

    @GET("orderitem")
    Call<List<OrderItem>> getOrderItemByOrder(@Query("orderID") int orderID);

    @GET("orderitem/full")
    Call<List<FullOrderItem>> getFullOrderItemByOrder(@Query("orderID") int orderID);

    @GET("category/forsale")
    Call<List<Category>> getCategoryList();

    @PUT("user/update/profile")
    Call<ResponseBody> updateProfile(@Body RequestBody userModel);

    @PUT("order/update/status")
    Call<Order> updateOrderStatus(@Query("orderID") int orderID, @Query("status") int status);

    @Multipart
    @PUT("user/update/image")
    Call<User> updateProfileImage(@Part("userID") RequestBody userID, @Part MultipartBody.Part image);

    @GET("order/desc")
    Call<List<Order>> getOrderDesc();

    @GET("order/status")
    Call<List<Order>> getOrderByStatus(@Query("status") int status);

    @GET("order/status/date")
    Call<List<Order>> getOrderByStatusAndDate(@Query("status") int status, @Query("month") int month, @Query("year") int year);

    @POST("product/update/status")
    Call<Product> updateStatus(@Query("productID") int productID, @Query("status") boolean status);

    @POST("product/update/quantity")
    Call<Product> updateQuantity(@Query("productID") int productID, @Query("quantity") int quantity);

    @GET("orderitem/revenue/byproduct")
    Call<List<ItemProduct>> getRevenueByProduct(@Query("month") int month, @Query("year") int year);

    @GET("review/bystar")
    Call<List<Review>> getReviewByStar(@Query("star") int star);

    @GET("review/all/desc")
    Call<List<Review>> getReviewDesc();

    @GET("order/byorderitem")
    Call<Order> getOrderByOrderItem(@Query("orderItemID") int orderItemID);

    @Multipart
    @POST("review/create")
    Call<Review> reviewOrderItem(@Part("orderItemID") RequestBody orderItemID, @Part("star") RequestBody star,
            @Part("comment") RequestBody comment, @Part MultipartBody.Part images);
}
