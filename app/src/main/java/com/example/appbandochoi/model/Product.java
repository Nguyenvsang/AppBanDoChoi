package com.example.appbandochoi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    private int productID;

    private String productName;

    private String description;

    private int quantity;

    private long price;

    private String images;
}
