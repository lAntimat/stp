package ru.studprof.studprof.adapter;

/**
 * Created by ������ on 10.09.2015.
 */
public class Comments {

    public String name;
    public String comment;
    public String date;
    public String imageUrl;
    public String commentId;
    public String commentIdParent;
    public String likeCount;
    public String whoLiked;
    public String vkUrl;
    public String commentContentImg;


    public Comments(String _name, String _comment, String _date, String _imageUrl, String _commentId, String _commentIdParent, String _likeCount, String _whoLiked, String _vkUrl, String _commentContentImg) {
        name = _name;
        comment = _comment;
        date = _date;
        imageUrl = _imageUrl;
        commentId = _commentId;
        commentIdParent = _commentIdParent;
        likeCount = _likeCount;
        whoLiked = _whoLiked;
        vkUrl = _vkUrl;
        commentContentImg = _commentContentImg;

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

    public String getLikeCount() {
        return likeCount;
    }

    public String getVkUrl() {
        return vkUrl;
    }

    public String getCommentContentImg() {
        return commentContentImg;
    }
}