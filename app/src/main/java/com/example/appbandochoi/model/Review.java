package com.example.appbandochoi.model;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements Serializable {
    private int reviewID;

    private int star;

    private String comment;

    private String images;

    private Timestamp createdAt;

    private Timestamp updatedAt;
    private Product product;

    private OrderItem orderItem;

    private User user;
}
