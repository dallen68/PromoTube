package com.google.step.youtube;

import com.google.auto.value.AutoValue;
import java.util.Date;

/**
 * Value type for a promotional code and information on its source. 
 */
@AutoValue
abstract class PromoCode {

    public static PromoCode create (String promoCode, int rating, String channelUrl, 
                                    String videoUrl, Date videoUploadDate) {
        return new AutoValue_PromoCode(promoCode, rating, channelUrl, videoUrl, videoUploadDate);
    }

    public abstract String getPromoCode();

    public abstract int getRating();

    public abstract String getChannelUrl();

    public abstract String getVideoUrl();

    public abstract Date getVideoUploadDate();
}

