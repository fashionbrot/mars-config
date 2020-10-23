package com.github.fashionbrot.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static String formatDate(DateTimeFormatter formatter, Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(formatter);
    }

    public static int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

}
