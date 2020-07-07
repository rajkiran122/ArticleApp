package com.example.articleapp.Models;

import android.net.Uri;

public class Users {


    private String biography;
    private String title;
    private String articleDetail;
    private String addedDate;
    private String articleImage;
    private String postKey;
    private String userName;
    private String userPhoto;
    private String userID;
    private String userEmail;

    public Users(String title, String articleDetail, String addedDate, String articleImage, String userName, String userPhoto, String userId, String userEmail, String biography, String postKey) {
        this.title = title;
        this.articleDetail = articleDetail;
        this.addedDate = addedDate;
        this.articleImage = articleImage;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userID = userId;
        this.userEmail = userEmail;
        this.biography = biography;
        this.postKey = postKey;
    }

    public Users() {
        //non parametarized constructor needed
    }

    public Users(String biography) {
        this.biography = biography;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public String getArticleImage() {
        return articleImage;
    }

    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleDetail() {
        return articleDetail;
    }

    public void setArticleDetail(String articleDetail) {
        this.articleDetail = articleDetail;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getArticleImageUri() {
        return articleImage;
    }

    public void setArticleImageUri(String articleImageUri) {
        this.articleImage = articleImageUri;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
