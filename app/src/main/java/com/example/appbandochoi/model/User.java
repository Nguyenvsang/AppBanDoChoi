package com.example.appbandochoi.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.appbandochoi.BR;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseObservable implements Serializable {

    private int userID;
    private String firstname;
    private String lastname;
    private int gender; // 0 for male; 1 for female; 2 for other
    private Timestamp birthDay;
    private String email;
    private String address;
    private String phone;
    private String image;
    private boolean role; // 0 for manager; 1 for customer
    private boolean status; // 0 for inactive; 1 for active
    private String username;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Bindable
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
        notifyPropertyChanged(BR.userID);
    }

    @Bindable
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        notifyPropertyChanged(BR.firstname);
    }

    @Bindable
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        notifyPropertyChanged(BR.lastname);
    }

    @Bindable
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }

    @Bindable
    public Timestamp getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Timestamp birthDay) {
        this.birthDay = birthDay;
        notifyPropertyChanged(BR.birthDay);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
        notifyPropertyChanged(BR.role);
    }

    @Bindable
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
