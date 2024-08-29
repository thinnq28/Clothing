package com.datn.shop_app.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateUtils {
    public LocalDate convertDateToLocalDate(Date date) {
        if(date == null) return null;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }
}
