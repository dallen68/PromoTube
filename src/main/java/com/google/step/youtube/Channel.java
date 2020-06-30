package com.google.step.youtube;

import com.google.auto.value.AutoValue;
import java.util.Date;

/**
 * Value type for a YouTube channel and information on its videos. 
 */
@AutoValue
abstract class Channel {

    public static Channel create (String channelId, String uploadPlaylistId, Date lastVideoDate,
                                  Date scrapeDate) {
        return new AutoValue_Channel(channelId, uploadPlaylistId, lastVideoDate, scrapeDate);
    }

    public abstract String getChannelId();

    public abstract String getUploadPlaylistId();

    public abstract Date getLastVideoDate();

    public abstract Date getScrapeDate();
}

