package com.google.step.youtube;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;

/**
 * Class for parsing the descriptions of YouTube videos for promotional codes
 * and affiliate links.
 */
public class DescriptionParser {

    private static final char DELIMITER = '\n';
    private static final int MAX_SNIPPET_LENGTH = 200;
    
    /**
     * Patterns used in regular expressions for parsing promocodes and affiliate links
     */
    @VisibleForTesting
    enum Patterns {

        CODE_NO_QUOTES(Pattern.compile("(?<=\\b(?i)code(?s).{1,2})([A-Z0-9][A-Za-z0-9\\-]+)")),
        CODE_WITH_QUOTES(Pattern.compile("(?<=\\b(?i)code(?s).{1,2}(\"|'))(.+?)(?=(\"|'))")),
        TO_AT_LINKS(Pattern.compile("(?<=\\b(?i)(to|at)(?s).{1,2})(https*:\\/\\/)[^\\s,\\)]+")),
        SYMBOL_NEAR_LINK(Pattern.compile(
                // check for link with $[0-9] or [0-9]% symbol <=100 chars before it
                  "((?<=(([0-9]%)|(\\$[0-9])).{1,100})(https*:\\/\\/)[^\\s,\\)]+)|"
                // check for link with $[0-9] or [0-9]% symbol <=100 chars after it
                + "(((https*:\\/\\/)[^\\s,\\)]+)(?=.{1,100}(([0-9]%)|(\\$[0-9]))))"));

        private final Pattern regex;

        Patterns(Pattern regex) {
            this.regex = regex;
        }

        public Pattern getPattern() {
            return this.regex;
        }
    }

    /**
     * Parses the given string for promotional codes and affiliate links in proximity to 
     * the given company name.
     * 
     * @param company The name of the company to find promocodes for.
     * @param description of the YouTube video to be parsed.
     * @return A list of all promotional codes and affiliate links found in the
     *         description in the same line as the company name (case insensitive), 
     *         duplicates allowed.
     */
    public static List<OfferSnippet> parseByCompany(String company, String description) {
        List<OfferSnippet> offers = new ArrayList<>();

        for (String snippet : description.split(String.valueOf(DELIMITER))) {
            if (snippet.toLowerCase().indexOf(company.toLowerCase()) != -1) {
                offers.addAll(parse(snippet));
            }
        }
        return offers;
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

        return removeDuplicateOffers(codes);
    }

    /**
     * Finds all matches in the given string from the given regex pattern.
     *
     * @param pattern of the regular experssion to use for matching.
     * @param description of the YouTube video to be parsed.
     * @return A list of all matches found in description.
     */
    @VisibleForTesting
    static List<OfferSnippet> findMatches(Pattern pattern, String description) {
        List<OfferSnippet> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(description);

        while (matcher.find()) {
            matches.add(OfferSnippet.create(matcher.group(), getBoundedSnippet(matcher.start(), description)));
        }
        return matches;
    }

    /*
     * Remove duplicate OfferSnippets from originalOffers. Note that the order of elements may change.
     */
    private static List<OfferSnippet> removeDuplicateOffers(List<OfferSnippet> originalOffers) {
        List<OfferSnippet> noDupsOffers = new ArrayList<>();
        Set<OfferSnippet> offerSet = new HashSet<>(originalOffers);
        noDupsOffers.addAll(offerSet);
        return noDupsOffers;
    }

    /*
     * Finds the snippet of description which conatins the target index. 
     * Bounds the snippet at MAX_SNIPPET_LENGTH characters and does not truncate words.
     */
    private static String getBoundedSnippet(int targetIndex, String description) {
        int startDelimiter = description.lastIndexOf(DELIMITER, targetIndex);
        int endDelimiter = description.indexOf(DELIMITER, targetIndex);

        // add 1 to not include delimiter in snippet
        int lineStart = startDelimiter < 0 ? 0 : startDelimiter + 1;
        int lineEnd = endDelimiter < 0 ? description.length() : endDelimiter;

        int startBoundIndex = targetIndex - (MAX_SNIPPET_LENGTH / 2);
        int endBoundIndex = targetIndex + (MAX_SNIPPET_LENGTH / 2);
        // could factor out description.lastIndexOf(" ", endBoundIndex) and indexOf

        int lastSpaceInBounds = description.lastIndexOf(" ", endBoundIndex);
        int firstSpaceInBounds = description.indexOf(" ", startBoundIndex);

        if (lineStart > startBoundIndex && endBoundIndex > lineEnd) {
            return description.substring(lineStart, lineEnd);
        } else if (lineStart > startBoundIndex) {
            int snippetEnd = lastSpaceInBounds < 0 ? endBoundIndex : lastSpaceInBounds;
            return description.substring(lineStart, snippetEnd) + " ...";
        } else if (endBoundIndex > lineEnd) {
            // add 1 to not include starting space in snippet
            int snippetStart = firstSpaceInBounds < 0 ? startBoundIndex : firstSpaceInBounds + 1;
            return "... " + description.substring(snippetStart, lineEnd);
        } else {
            // add 1 to not include starting space in snippet
            int snippetStart = firstSpaceInBounds < 0 ? startBoundIndex : firstSpaceInBounds + 1;
            int snippetEnd = lastSpaceInBounds < 0 ? endBoundIndex : lastSpaceInBounds;
            return "... " + description.substring(snippetStart, snippetEnd) + " ...";
        }
    }

}
