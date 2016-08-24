package ru.studprof.studprof.adapter;

import java.io.Serializable;

/**
 * Created by Ильназ on 26.12.2015.
 */
public class DataObject implements Serializable {
    private String whoPhoto;
    private String title;
    private String shortDescription;
    private String mData;
    private String mImgUrl;
    private String mAlbumUrl;
    private String mCountOfPhotos;
    private String mCountOfVisit;
    private String albumUrls;

    public DataObject(String _whoPhoto, String _title, String _shortDescription, String data, String imgUrl, String countOfPhotos, String countOfVisit, String _albumUrls){
        whoPhoto = _whoPhoto;
        title = _title;
        shortDescription = _shortDescription;
        mImgUrl = imgUrl;
        mData = data;
        mCountOfPhotos = countOfPhotos;
        mCountOfVisit = countOfVisit;
        albumUrls = _albumUrls;

    }

    public String getWhoPhoto() {
        return whoPhoto;
    }

    public void setWhoPhoto(String whoPhoto) {
        this.whoPhoto = whoPhoto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getmImgUrl() {
        return mImgUrl;
    }

    public String getmData() {
        return mData;
    }

    public String getmAlbumUrl() {
        return mAlbumUrl;
    }

    public void setmCountOfPhotos(String mCountOfPhotos) {
        this.mCountOfPhotos = mCountOfPhotos;
    }

    public String getmCountOfPhotos() {
        return mCountOfPhotos;
    }

    public void setmCountOfVisit(String mCountOfVisit) {
        this.mCountOfVisit = mCountOfVisit;
    }

    public String getmCountOfVisit() {
        return mCountOfVisit;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getAlbumUrls() {
        return albumUrls;
    }

    public void setAlbumUrls(String albumUrls) {
        this.albumUrls = albumUrls;
    }
}
