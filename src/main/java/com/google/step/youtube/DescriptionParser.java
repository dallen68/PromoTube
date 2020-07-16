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

    // package private in order to access in tests
    static final int MAX_SNIPPET_LENGTH = 200;
    
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


    // Finds the snippet (line) of description which conatins the promocode index. 
    // Truncates the snippet at MAX_SNIPPET_LENGTH characters.
    private static String getSnippet(int promocodeIndex, String description) {
        String delineator = "\n";

        int startDelineator = description.lastIndexOf(delineator, promocodeIndex);
        int startSnippet = startDelineator == -1 ? 
                Math.max(0, promocodeIndex - MAX_SNIPPET_LENGTH/2) :
                Math.max(promocodeIndex - MAX_SNIPPET_LENGTH/2, startDelineator + delineator.length());

        int endDelineator = description.indexOf(delineator, promocodeIndex);
        int endSnippet = endDelineator == -1 ? 
                Math.min(description.length(), promocodeIndex + MAX_SNIPPET_LENGTH/2) :
                Math.min(promocodeIndex + MAX_SNIPPET_LENGTH/2, endDelineator);

        return description.substring(startSnippet, endSnippet);
    }

}
