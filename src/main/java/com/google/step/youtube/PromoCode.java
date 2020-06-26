package com.google.step.youtube;

import java.util.Date;

/**
 * Object for a promotional code and information on its source. 
 */
public class PromoCode {

    private String promoCode;
    private int rating;
    // URLs will be strored as Strings to match type in datastore
    private String channelUrl;
    private String videoUrl;
    private Date videoUploadDate;

    public PromoCode (String promoCode, int rating, String channelUrl, String videoUrl, Date videoUploadDate) {
        this.promoCode = promoCode;
        this.rating = rating;
        this.channelUrl = channelUrl;
        this.videoUrl = videoUrl;
        this.videoUploadDate = videoUploadDate;
    }

    public void setPromoCode (String promoCode) {
        this.promoCode = promoCode;
    }

    public void setRating (int rating) {
        this.rating = rating;
    }

    public void setChannelUrl (String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public void setVideoUrl (String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setVideoUploadDate (Date videoUploadDate) {
        this.videoUploadDate = videoUploadDate;
    }

    public String getPromoCode () {
        return promoCode;
    }

    public int setRating () {
        return rating;
    }

    public String getChannelUrl () {
        return channelUrl;
    }

    public String getVideoUrl () {
        return videoUrl;
    }

    public Date getVideoUploadDate () {
        return videoUploadDate;
    }

    public String toString() {
        return "Promocode: " + promoCode + "\nRating: " + rating + "\nVideo: " + videoUrl 
                + "\nUploaded on: " + videoUploadDate.toString() + "\nFrom channel: " + channelUrl;
    }
}

