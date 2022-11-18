package com.adiupd123.talkrr.Models;

public class Status {
    private String statusImageUrl;
    private long timeStamp;

    public Status(){}

    public Status(String statusImageUrl, long timeStamp) {
        this.statusImageUrl = statusImageUrl;
        this.timeStamp = timeStamp;
    }

    public String getStatusImageUrl() {
        return statusImageUrl;
    }

    public void setStatusImageUrl(String statusImageUrl) {
        this.statusImageUrl = statusImageUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
