package com.example.leochris.launcher.announcement;

import android.util.StringBuilderPrinter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 7/9/2017.
 */

public class Event {

    public String title;
    public String text;
    public Boolean event;
    public Boolean important;
    public Long sdate;
    public Long edate;


    public Event() {
    }

    public Event(String title, String text, Boolean event, Boolean important, Long sdate, Long edate) {
        this.title = title;
        this.text = text;
        this.event = event;
        this.important = important;
        this.sdate = sdate;
        this.edate = edate;
    }

    public Long getSDate() {
        return sdate;
    }

    public Long getEDate(){
        return edate;
    }

    public String getTitle() {
        return title;
    }

    public String getText(){
        return text;
    }

    public boolean getEvent(){
        return event;
    }

    public boolean getImportant(){
        return important;
    }

    // Returns string in the format of (startdate-enddate)
    public String getDate(){
        return dateLongToString(sdate) + " - " + dateLongToString(edate);
    }

    public static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
    public static final DateFormat df2 = new SimpleDateFormat("EEE, MMM d");

    // Converts date from Firebase (type Long) to String with format (EEE, MMM d)
    public String dateLongToString(long date){
        try {
            Date d = df.parse(Long.toString(date));
            return df2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString(){
        StringBuilder strRet = new StringBuilder();
        strRet.append("title:");
        strRet.append(title);
        strRet.append(",");
        strRet.append("text:");
        strRet.append(text);
        strRet.append(",");
        strRet.append("event:");
        strRet.append(event);
        strRet.append(",");
        strRet.append("important:");
        strRet.append(important);
        strRet.append(",");
        strRet.append("sdate:");
        strRet.append(sdate);
        strRet.append(",");
        strRet.append("edate:");
        strRet.append(edate);
        return strRet.toString();
    }
}
