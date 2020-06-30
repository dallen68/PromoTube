package com.google.step.youtube;

import java.util.Date;

/**
 * Object for a YouTube channel and information on its videos. 
 */
public class Channel {

    private String channelUrl;
    private String uploadPlaylistId;
    private Date lastVideoDate;
    private Date scrapeDate;

    public Channel (String channelUrl, String uploadPlaylistId, Date lastVideoDate, Date scrapeDate) {
        this.channelUrl = channelUrl;
        this.uploadPlaylistId = uploadPlaylistId;
        this.lastVideoDate = lastVideoDate;
        this.scrapeDate = scrapeDate;
    }

    public void setChannelUrl (String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public void getUploadPlaylistId (String uploadPlaylistId) {
        this.uploadPlaylistId = uploadPlaylistId;
    }

    public void setLastVideoDate (Date lastVideoDate) {
        this.lastVideoDate = lastVideoDate;
    }

    public void setScrapeDate (Date scrapeDate) {
        this.scrapeDate = scrapeDate;
    }

    public String getChannelUrl () {
        return channelUrl;
    }

    public String getUploadPlaylistId () {
        return uploadPlaylistId;
    }

    public Date getLastVideoDate () {
        return lastVideoDate;
    }

    public Date getScrapeDate () {
        return scrapeDate;
    }

    public String toString() {
        return "Channel: " + channelUrl + "\nUploads Playlist: " + uploadPlaylistId
                + "\nLast video uploaded on: " + lastVideoDate.toString() 
                + "\nScrape date: " + scrapeDate.toString();
    }
}

