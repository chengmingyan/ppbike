package com.ppbike.bean;

import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/7/15.
 */
public class CommentListResult {
    private int count;
    private int average;
    private ArrayList<CommentResult> comments;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public ArrayList<CommentResult> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentResult> comments) {
        this.comments = comments;
    }
}
