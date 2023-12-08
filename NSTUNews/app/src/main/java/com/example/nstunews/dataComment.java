package com.example.nstunews;

public class dataComment {
    String commentId;
    String comment;
    String uid;

    public dataComment() {

    }

    public dataComment(String commentId, String comment, String uid) {
        this.commentId = commentId;
        this.comment = comment;
        this.uid = uid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
