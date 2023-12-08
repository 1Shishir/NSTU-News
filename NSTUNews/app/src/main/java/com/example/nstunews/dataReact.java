package com.example.nstunews;

public class dataReact {
    boolean react;
    String uid;

    public dataReact(){}

    public dataReact(Boolean react, String uid) {
        this.react = react;
        this.uid = uid;
    }

    public Boolean getReact() {
        return react;
    }

    public void setReact(boolean react) {
        this.react = react;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
