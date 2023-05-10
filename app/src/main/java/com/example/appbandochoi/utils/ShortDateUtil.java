package com.example.appbandochoi.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShortDateUtil {
    public String parseShortDate(Timestamp date){
        String formattedDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            Date parsedDate = sdf.parse(String.valueOf(date));
            sdf.applyPattern("dd-MM-yyyy");
            formattedDate = sdf.format(parsedDate);
        } catch (
                ParseException e) {
        }
        return formattedDate;
    }
}
