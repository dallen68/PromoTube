package com.google.step.youtube;

import com.google.auto.value.AutoValue;
import java.util.Date;

/**
 * Value type for a promotional code and information on its source.
 */
@AutoValue
public abstract class PromoCode {

    public abstract String getPromoCode();

    public abstract String getSnippet();

    public abstract String getVideoId();

    public abstract String getVideoTitle();

    public abstract Date getVideoUploadDate();

    public static Builder builder() {
        return new AutoValue_PromoCode.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder setPromoCode(String promocode);

        public abstract Builder setSnippet(String snippet);

        public abstract Builder setVideoId(String videoId);

        public abstract Builder setVideoTitle(String videoTitle);

        public abstract Builder setVideoUploadDate(Date uploadDate);

        public abstract PromoCode build();
    }
}
