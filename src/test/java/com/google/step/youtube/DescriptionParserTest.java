package com.google.step.youtube;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DescriptionParserTest {

    /*                                                                   *
     * ==================== GENERAL TESTS FOR PARSE ==================== *
     *                                                                   */

    /** String will return no matches from all regexs in parse() */
    @Test
    public void parse_matchNone() {
        String desc = "Check out our UPDATED version which has all the NEW ELEMENTS here:  https://youtu.be/rz4Dd1I_fX0"
                        + "\nThe TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                        + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";

        Assert.assertEquals(Collections.emptyList(), DescriptionParser.parse(desc));
    }

    /** String will return matches from all regexs in parse() */
    @Test
    public void parse_matchAll() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        Assert.assertEquals(Arrays.asList("FUNGBROS10"), DescriptionParser.parse(desc));
    }

    /** String will only have matches from CODE_NO_QUOTES regex */
    @Test
    public void parse_codeNoQuotes() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        Assert.assertEquals(Arrays.asList("FUNGBROS10"), DescriptionParser.parse(desc));
    }

    // TODO(margaret): when 2nd regex is added, test for allowing duplicate promocodes


    /*                                                                   *
     * ===================== TEST 'CODE' NO QUOTES ===================== *
     *                         ex. "code OFF20"                          */

    private static final Pattern CODE_NO_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_NO_QUOTES.getPattern();

    /** CODE_NO_QUOTES: standard case - 1 space, end of string, all caps, letters and numbers */
    @Test
    public void codeNoQuotes_standard() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("FUNGBROS10"), actual);
    }

    /** CODE_NO_QUOTES: case sensitivity - match promocode until first lowercase */
    @Test
    public void codeNoQuotes_promocodeCaseSensitive() {
        String desc = "Use code LINUSsssS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("LINUS"), actual);
    }

    /** CODE_NO_QUOTES: case insensitivity for keyword 'code' */
    @Test
    public void codeNoQuotes_keywordCaseInsensitive() {
        String desc = "Use cOdE LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("LINUS"), actual);
    }

    /** CODE_NO_QUOTES: edge case - 1 letter promocode is not matched */
    @Test
    public void codeNoQuotes_1LetterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code R at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Collections.emptyList(), actual);
    }

    /** CODE_NO_QUOTES: edge case - 2 letter promocode is matched */
    @Test
    public void codeNoQuotes_2LetterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code RO at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("RO"), actual);
    }

    /** CODE_NO_QUOTES: lookbehind not lookahead - promocode is before word 'code' */
    @Test
    public void codeNoQuotes_lookbehind() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the ROOSTER code at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Collections.emptyList(), actual);
    }

    /** CODE_NO_QUOTES: more than 1 space between 'code' and promocode is not matched */
    @Test
    public void codeNoQuotes_2Spaces() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code  ROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Collections.emptyList(), actual);
    }

    /** CODE_NO_QUOTES: newline between 'code' and promocode is acceptable */
    @Test
    public void codeNoQuotes_newLine() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code\nROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("ROOSTER"), actual);
    }

    /** CODE_NO_QUOTES: special charaters and symbols not included in match */
    @Test
    public void codeNoQuotes_symbols() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code ROOSTER! at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("ROOSTER"), actual);
    } 

    /** CODE_NO_QUOTES: 'code' at very beginning of string */
    @Test
    public void codeNoQuotes_keywordAtStart() {
        String desc = "code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("LINUS"), actual);
    }

    /** CODE_NO_QUOTES: 'code' at very end of string */
    @Test
    public void codeNoQuotes_keywordAtEnd() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code BOULDERINGBOBAT";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("BOULDERINGBOBAT"), actual);
    }

    /** CODE_NO_QUOTES: promocode made of all numbers is still matched */
    @Test
    public void codeNoQuotes_promocodeAllNumbers() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code 12345";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Arrays.asList("12345"), actual);
    }

    /** CODE_NO_QUOTES: String must have standalone 'code' word */
    @Test
    public void codeNoQuotes_standaloneKeyword() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using Decode BOULDERINGBOBAT";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        Assert.assertEquals(Collections.emptyList(), actual);
    } 

    /** CODE_NO_QUOTES: multiple matches in 1 String, order does not matter */
    @Test
    public void codeNoQuotes_multipleMatches() {
        String desc = "This episode originally recorded June 8, 2020, and is sponsored by Stamps.com "
            + "(Go to http://stamps.com, click on the microphone at the top of the homepage, "
            + "and type in code ROOSTERTEETH to claim your special offer), Mercari "
            + "(Buy or sell almost anything on Mercari on the App store or at http://mercari.com), "
            + "and Bespoke Post (Get 20% off your first monthly box when you sign up at http://boxofawesome.com "
            + "and enter the code ROOSTER at checkout!).";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        List<String> expected = Arrays.asList("ROOSTERTEETH", "ROOSTER");
        Assert.assertTrue(actual.containsAll(expected));
    }

}

