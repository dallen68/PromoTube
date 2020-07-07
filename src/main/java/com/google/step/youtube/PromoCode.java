package com.google.step.youtube;

import com.google.auto.value.AutoValue;
import java.util.Date;

/**
 * Value type for a promotional code and information on its source. 
 */
@AutoValue
abstract class PromoCode {

    public static PromoCode create (String promoCode, String videoId, Date videoUploadDate) {
        return new AutoValue_PromoCode(promoCode,videoId, videoUploadDate);
    }

    public abstract String getPromoCode();

    public abstract String getVideoId();

    public abstract Date getVideoUploadDate();
}

