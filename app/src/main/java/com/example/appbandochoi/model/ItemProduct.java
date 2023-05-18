package com.example.appbandochoi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemProduct implements Serializable {

    // Product
    private int productID;

    private String productName;

    private String productImage;

    // Order Item
    private int quantity;

    private long total;

}
