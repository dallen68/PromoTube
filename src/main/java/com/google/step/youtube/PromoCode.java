package com.google.step.youtube;

import com.google.auto.value.AutoValue;
import java.util.Date;

/**
 * Value type for a promotional code and information on its source.
 */
@AutoValue
public abstract class PromoCode {

    public static PromoCode create(String promoCode, String snippet, String videoId, String videoTitle, Date videoUploadDate) {
        return new AutoValue_PromoCode(promoCode, snippet, videoId, videoTitle, videoUploadDate);
    }

    public abstract String getPromoCode();

    public abstract String getSnippet();

    public abstract String getVideoId();

    public abstract String getVideoTitle();

    public abstract Date getVideoUploadDate();
}
