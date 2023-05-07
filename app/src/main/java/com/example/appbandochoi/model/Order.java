package com.example.appbandochoi.model;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private int orderID;

    private Timestamp orderedDate;

    private Timestamp receivedDate;

    private Timestamp cancelledDate;

    private int status; // 0 for pending pay; 1 for delivering; 2 for received; 3 for cancelled

    private String receiverName;

    private String phone;

    private String address;

    private long total;
}
