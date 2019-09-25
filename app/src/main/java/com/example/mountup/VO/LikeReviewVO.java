package com.example.mountup.VO;

public class LikeReviewVO {
    int reviewID;
    int count;

    public LikeReviewVO() {
    }

    public LikeReviewVO(int reviewID, int count) {
        this.reviewID = reviewID;
        this.count = count;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
