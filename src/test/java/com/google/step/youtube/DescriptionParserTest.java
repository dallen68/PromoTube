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
    public void parse_MatchNone() {
        String desc = "Check out our UPDATED version which has all the NEW ELEMENTS here:  https://youtu.be/rz4Dd1I_fX0"
                        + "\nThe TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                        + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";

        List<String> actual = DescriptionParser.parse(desc);
        List<String> expected = Collections.emptyList();
        Assert.assertEquals(expected, actual);
    }

    /** String will return matches from all regexs in parse() */
    @Test
    public void parse_MatchAll() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        List<String> actual = DescriptionParser.parse(desc);
        List<String> expected = Arrays.asList("FUNGBROS10");
        Assert.assertEquals(expected, actual);
    }

    /** String will only have matches from CODE_NO_QUOTES regex */
    @Test
    public void parse_CODE_NO_QUOTES() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        List<String> actual = DescriptionParser.parse(desc);
        List<String> expected = Arrays.asList("FUNGBROS10");
        Assert.assertEquals(expected, actual);
    }


    /*                                                                   *
     * ===================== TEST 'CODE' NO QUOTES ===================== *
     *                         ex. "code OFF20"                          */

    private final Pattern CODE_NO_QUOTES = DescriptionParser.Patterns.CODE_NO_QUOTES.getPattern();

    /** Test standard case: 1 space, end of string, all caps, letters and numbers */
    @Test
    public void codeNoQuotes_Standard() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Arrays.asList("FUNGBROS10");
        Assert.assertEquals(expected, actual);
    }

    /** Test case sensitivity: match promocode until first lowercase */
    @Test
    public void codeNoQuotes_PromocodeCaseSensitive() {
        String desc = "Use code LINUSsssS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Arrays.asList("LINUS");
        Assert.assertEquals(expected, actual);
    }

    /** Test case insensitivity for keyword 'code' */
    @Test
    public void codeNoQuotes_KeywordCaseInsensitive() {
        String desc = "Use cOdE LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Arrays.asList("LINUS");
        Assert.assertEquals(expected, actual);
    }

    /** Test edge case: 1 letter promocode is not matched */
    @Test
    public void codeNoQuotes_1LetterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com and enter the code R at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Collections.emptyList();
        Assert.assertEquals(expected, actual);
    }

    /** Test edge case: 2 letter promocode is matched */
    @Test
    public void codeNoQuotes_2LetterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com and enter the code RO at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Arrays.asList("RO");
        Assert.assertEquals(expected, actual);
    }

    /** Test lookbehind not lookahead: promocode is before word 'code' */
    @Test
    public void codeNoQuotes_Lookbehind() {
        String desc = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com and enter the ROOSTER code at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Collections.emptyList();
        Assert.assertEquals(expected, actual);
    }

    /** Test more than 1 space between 'code' and promocode is not matched */
    @Test
    public void codeNoQuotes_2Spaces() {
        String desc = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com and enter the code  ROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Collections.emptyList();
        Assert.assertEquals(expected, actual);
    }

    /** Test newline between 'code' and promocode is acceptable */
    @Test
    public void codeNoQuotes_NewLine() {
        String desc = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com and enter the code\nROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES, desc);
        List<String> expected = Arrays.asList("ROOSTER");
        Assert.assertEquals(expected, actual);
    }



    // test promocode has symbol no match
    // test promocode lowercase letters partial match
    // "code" at beginning of text
    // promocode at very end of string
    // "code" in middle
    // promocode followed by "code" no match
    // test just letter and numbers code ok
    // test just letters code ok
    // test just numbers code .... ok?
    // "code" in longer word no match
    // test this case: "decode HELLO" no match
    // test duplicates allowed
    // multiple matches in 1 string
    // all caps right before 'code'
    // // order should not matter in returned list, maybe better to use contains all both ways since will allow duplicates. make helper and assert as true

}

