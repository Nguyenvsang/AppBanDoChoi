package com.example.appbandochoi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem  implements Serializable {
    private int cartItemID;

    private int quantity;

    private Product product;
}
