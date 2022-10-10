package com.example.soundrecorder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public String getTimeAgo(long duration){
        Date now =  new Date();
        long seconds = TimeUnit.MICROSECONDS.toSeconds(now.getTime()-duration);
        long minutes = TimeUnit.MICROSECONDS.toMinutes(now.getTime()-duration);
        long hours = TimeUnit.MICROSECONDS.toHours(now.getTime()-duration);
        long days = TimeUnit.MICROSECONDS.toDays(now.getTime()-duration);
        if (seconds < 60 ){
            return "vài giây trước";
        }else if(minutes == 1) {
            return "1 phút trước";
        }else if(minutes >1 && minutes < 60){
            return minutes+ " phút ";
        } else if(hours ==1){
            return "1 giờ trước";
        } else if(hours >1 && hours <24){
            return hours + "giờ";
        } else if (days == 1) {
            return "1 ngày trước";
        }else {
            return days + "ngày";
        }
    }
}
