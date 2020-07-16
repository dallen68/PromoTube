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
        for (int i = description.toLowerCase().indexOf(company.toLowerCase()); i != -1;) {
            String snippet = getCompleteSnippet(i, description);
            description = description.substring(i + company.length() - 1);
            offers.addAll(parse(snippet));
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
            matches.add(OfferSnippet.create(matcher.group(), getTrimmedSnippet(matcher.start(), description)));
        }
        return matches;
    }

    // Finds the snippet (line) of description which conatins the target index. 
    // Bounds the snippet at MAX_SNIPPET_LENGTH characters and does not truncate words.
    private static String getTrimmedSnippet(int targetIndex, String description) {
        String completeSnippet = getCompleteSnippet(targetIndex, description);
        if (completeSnippet.length() <= MAX_SNIPPET_LENGTH) {
            return completeSnippet;
        }

        int startIndexByWord = description.indexOf(" ", targetIndex - MAX_SNIPPET_LENGTH/2) + 1;
        int endIndexByWord = description.lastIndexOf(" ", targetIndex + MAX_SNIPPET_LENGTH/2);
        return description.substring(startIndexByWord, endIndexByWord);
    }

    // Finds the snippet (line) of description which conatins the target index. 
    private static String getCompleteSnippet(int targetIndex, String description) {
        String delineator = "\n";
        int startDelineator = description.lastIndexOf(delineator, targetIndex);
        int endDelineator = description.indexOf(delineator, targetIndex);

        int startSnippet = startDelineator == -1 ? 0 : startDelineator + delineator.length();
        int endSnippet = endDelineator == -1 ? description.length() : endDelineator;
        return description.substring(startSnippet, endSnippet);
    }

}
