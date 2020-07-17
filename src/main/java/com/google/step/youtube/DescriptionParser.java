package com.google.step.youtube;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Class for parsing the descriptions of YouTube videos for promotional codes
 * and affiliate links.
 */
public class DescriptionParser {

    @VisibleForTesting
    static final int MAX_SNIPPET_LENGTH = 200;
    
    /**
     * Patterns used in regular expressions for parsing promocodes and affiliate links
     */
    @VisibleForTesting
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

    /* 
     * Finds the snippet (line) of description which conatins the promocode index. 
     * Bounds the snippet at MAX_SNIPPET_LENGTH characters and does not truncate words. 
     */
    private static String getSnippet(int promocodeIndex, String description) {
        char delimiter = '\n';
        int startDelimiter = description.lastIndexOf(delimiter, promocodeIndex);
        int endDelimiter = description.indexOf(delimiter, promocodeIndex);

        // add 1 to not include delimiter in snippet
        int snippetStart = startDelimiter == -1 ? 0 : startDelimiter + 1;
        int snippetEnd = endDelimiter == -1 ? description.length() : endDelimiter;
        String completeSnippet = description.substring(snippetStart, snippetEnd);

        if (completeSnippet.length() <= MAX_SNIPPET_LENGTH) {
            return completeSnippet;
        }

        // add 1 to not include the space in snippet
        int startIndexByWord = description.indexOf(" ", promocodeIndex - MAX_SNIPPET_LENGTH / 2) + 1;
        int endIndexByWord = description.lastIndexOf(" ", promocodeIndex + MAX_SNIPPET_LENGTH / 2);
        return description.substring(startIndexByWord, endIndexByWord);
    }

}
