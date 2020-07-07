package com.google.step.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Class for parsing the descriptions of YouTube videos for promotional codes and affiliate links. 
 */
public class DescriptionParser {

    // enum is package private in order to access in tests
    enum Patterns {
        
        CODE_NO_QUOTES(Pattern.compile("(?<=\\b(?i)code\\s)([A-Z0-9]{2,})"));

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
     * @return A list of all promotional codes and affiliate links found in the description, duplicates allowed.
     */
    public static List<String> parse(String description) {
        List<String> codes = new ArrayList<>();

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
    public static List<String> findMatches(Pattern pattern, String description) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(description);

        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }
  
}

