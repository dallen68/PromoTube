package com.google.step.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Class for parsing the descriptions of YouTube videos for promotional codes
 * and affiliate links.
 */
public class DescriptionParser {

    // enum is package private in order to access in tests
    enum Patterns {

        CODE_NO_QUOTES(Pattern.compile("(?<=\\b(?i)code\\s)([A-Z0-9]{2,})")),
        CODE_WITH_QUOTES(Pattern.compile("(?<=\\b(?i)code\\s(\"|'))(.+?)(?=(\"|'))")),
        TO_AT_LINKS(Pattern.compile("(?<=\\b(?i)(to|at)\\s)(https*:\\/\\/)[^\\s,\\)]+"));

        private final Pattern regex;

        Patterns(Pattern regex) {
            this.regex = regex;
        }

        public Pattern getPattern() {
            return this.regex;
        }
    }

    /**
     * Parses the given string for promotional codes and affiliate links.
     *
     * @param description of the YouTube video to be parsed.
     * @return A list of all promotional codes and affiliate links found in the
     *         description, duplicates allowed.
     */
    public static List<OfferSnippet> parse(String description) {
        List<OfferSnippet> codes = new ArrayList<>();

        for (Patterns regex : Patterns.values()) {
            codes.addAll(findMatches(regex.getPattern(), description));
        }
        return codes;
    }

    /**
     * Finds all matches in the given string from the given regex pattern.
     *
     * @param pattern of the regular experssion to use for matching.
     * @param description of the YouTube video to be parsed.
     * @return A list of all matches found in description.
     */
    public static List<OfferSnippet> findMatches(Pattern pattern, String description) {
        List<OfferSnippet> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(description);

        while (matcher.find()) {
            matches.add(OfferSnippet.create(matcher.group(), getSnippet(matcher.start(), description)));
        }
        return matches;
    }

    /**
     * Finds the snippet (paragraph) of description which conatins the promocode index.
     * 
     * @param promocodeIndex The index in the description which marks the start of the promocode.
     * @param description of the video from which the promocode was extracted.
     * @return The snippet (paragraph) which contains the promocodeIndex. A paragraph delineated by 
     *         two (2) newline characters (\n).
     */
    private static String getSnippet(int promocodeIndex, String description) {
        String delineator = "\n\n";

        int startDelineator = description.lastIndexOf(delineator, promocodeIndex);
        int startSnippet = startDelineator == -1 ? 0 : startDelineator + delineator.length();

        int endDelineator = description.indexOf(delineator, promocodeIndex);
        int endSnippet = endDelineator == -1 ? description.length() : endDelineator;

        return description.substring(startSnippet, endSnippet);
    }

}
