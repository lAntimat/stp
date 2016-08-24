package ru.studprof.studprof.adapter;

/**
 * Created by ������ on 10.09.2015.
 */
public class FeedBack {

    public String name;
    public String comment;
    public String date;
    public String imageUrl;
    public String commentId;
    public String commentIdParent;
    public String feedPath;
    public String whoLiked;
    public String vkUrl;
    public String commentContentImg;
    public String feedUrl;


    public FeedBack(String _name, String _comment, String _date, String _imageUrl, String _commentId, String _commentIdParent, String _feedPath, String _whoLiked, String _vkUrl, String _commentContentImg, String _feedUrl) {
        name = _name;
        comment = _comment;
        date = _date;
        imageUrl = _imageUrl;
        commentId = _commentId;
        commentIdParent = _commentIdParent;
        feedPath = _feedPath;
        whoLiked = _whoLiked;
        vkUrl = _vkUrl;
        commentContentImg = _commentContentImg;
        feedUrl = _feedUrl;

    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentIdParent() {
        return commentIdParent;
    }

    public String getVkUrl() {
        return vkUrl;
    }

    public String getCommentContentImg() {
        return commentContentImg;
    }


}