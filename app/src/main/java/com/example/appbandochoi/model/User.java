package com.example.appbandochoi.model;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

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
}
