package org.eventservice.hibernate.reactive.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
   private static SimpleDateFormat dateTimeSdf;
   private static SimpleDateFormat dateSdf;
    static {
        dateTimeSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTimeSdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateSdf = new SimpleDateFormat("dd/MM/yyyy");
        dateSdf.setTimeZone(TimeZone.getTimeZone("UTC"));

    }

    public static SimpleDateFormat getDateTimeFormat(){
        return dateTimeSdf;
    }
    public static SimpleDateFormat getDateFormat(){
        return dateSdf;
    }

    public static Date getCurrentUtcDate(){
        return new Date();
    }

}
