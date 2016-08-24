package ru.studprof.studprof.adapter;

/**
 * Created by ������ on 10.09.2015.
 */
public class VideoDescribe {

    public String name;
    public String duration;
    public String date;
    public String imageUrl;
    public String url;


    public VideoDescribe(String _name, String _duration, String _date, String _imageUrl, String _url) {
        name = _name;
        duration = _duration;
        date = _date;
        imageUrl = _imageUrl;
        url = _url;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }
}