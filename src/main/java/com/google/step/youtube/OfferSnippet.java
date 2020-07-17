package com.google.step.youtube;

import com.google.auto.value.AutoValue;

/**
 * Value type for a promotional code and the text snippet it was extracted from.
 */
@AutoValue
public abstract class OfferSnippet {

    public static OfferSnippet create (String promoCode, String snippet) {
        return new AutoValue_OfferSnippet(promoCode, snippet);
    }

    public abstract String getPromoCode();

    public abstract String getSnippet();
}
