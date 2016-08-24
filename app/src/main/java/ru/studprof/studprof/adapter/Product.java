package ru.studprof.studprof.adapter;

import java.io.Serializable;

/**
 * Created by ������ on 10.09.2015.
 */
public class Product implements Serializable {

    public String name;
    public String price;
    public String date;
    public String image;
    public String url;
    public String visitCount;
    public String commentCount;


    public Product(String _describe, String _price, String _date, String _image, String _url, String _visitCount, String _commentCount) {
        name = _describe;
        price = _price;
        date = _date;
        image = _image;
        url = _url;
        visitCount = _visitCount;
        commentCount = _commentCount;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

}