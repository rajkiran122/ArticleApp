package com.example.articleapp.Models;

public class Comments{

    String userId,userImg,content,userName,postedDate,postedTime;

    public Comments() {
        //No args constructor needed
    }

    public String getUserId() {
        return userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getPostedTime() {
        return postedTime;
    }

    public Comments(String userId, String userImg, String content, String userName, String postedDate, String postedTime) {
        this.userId = userId;
        this.userImg = userImg;
        this.content = content;
        this.userName = userName;
        this.postedDate = postedDate;
        this.postedTime = postedTime;
    }
}
