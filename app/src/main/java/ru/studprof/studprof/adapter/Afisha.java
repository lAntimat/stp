package ru.studprof.studprof.adapter;

/**
 * Created by ������ on 10.09.2015.
 */
public class Afisha {

    public String title;
    public String subTitle;
    public String func;
    public String place;
    public String data;
    public String dayOfWeek;


    public Afisha(String title, String subTitle, String func, String place, String data, String dayOfWeek) {
        this.title = title;
        this.subTitle = subTitle;
        this.func = func;
        this.place = place;
        this.data = data;
        this.dayOfWeek = dayOfWeek;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}