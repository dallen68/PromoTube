package com.google.step.youtube;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
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

        assertThat(Collections.emptyList(), equalTo(DescriptionParser.parse(desc)));
    }

    /** String will return matches from all regexs in parse() */
    @Test
    public void parse_matchAll() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10. "
            + "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\".";

        assertThat(Arrays.asList("FUNGBROS10", "COOLIRPA"), equalTo(DescriptionParser.parse(desc)));
    }

    /** String will only have matches from CODE_NO_QUOTES regex */
    @Test
    public void parse_codeNoQuotes() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        assertThat(Arrays.asList("FUNGBROS10"), equalTo(DescriptionParser.parse(desc)));
    }

    /** String will only have matches from CODE_WITH_QUOTES regex */
    @Test
    public void parse_codeWithQuotes() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
        + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\". ";

        assertThat(Arrays.asList("COOLIRPA"), equalTo(DescriptionParser.parse(desc)));
    }


    /*                                                                   *
     * ===================== TEST 'CODE' NO QUOTES ===================== *
     *                         ex. "code OFF20"                          */

    private static final Pattern CODE_NO_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_NO_QUOTES.getPattern();

    /** CODE_NO_QUOTES: letters and numbers */
    @Test
    public void codeNoQuotes_lettersAndNumbers() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("FUNGBROS10"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: case sensitivity - match promocode until first lowercase */
    @Test
    public void codeNoQuotes_promocodeCaseSensitive() {
        String desc = "Use code LINUSsssS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("LINUS"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: case insensitivity for keyword 'code' */
    @Test
    public void codeNoQuotes_keywordCaseInsensitive() {
        String desc = "Use cOdE LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("LINUS"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: edge case - 1 chararcter promocode is not matched */
    @Test
    public void codeNoQuotes_1ChararcterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code R at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_NO_QUOTES: edge case - 2 chararcter promocode is matched */
    @Test
    public void codeNoQuotes_2CharacterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code RO at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("RO"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: lookbehind not lookahead - promocode is before word 'code' */
    @Test
    public void codeNoQuotes_lookbehind() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the ROOSTER code at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_NO_QUOTES: more than 1 space between 'code' and promocode is not matched */
    @Test
    public void codeNoQuotes_2Spaces() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code  ROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_NO_QUOTES: newline between 'code' and promocode is acceptable */
    @Test
    public void codeNoQuotes_newLine() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code\nROOSTER at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("ROOSTER"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: special charaters and symbols not included in match */
    @Test
    public void codeNoQuotes_symbols() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code ROOSTER! at checkout!";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("ROOSTER"), equalTo(actual));
    } 

    /** CODE_NO_QUOTES: 'code' at very beginning of string */
    @Test
    public void codeNoQuotes_keywordAtStart() {
        String desc = "code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("LINUS"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: 'code' at very end of string */
    @Test
    public void codeNoQuotes_keywordAtEnd() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code BOULDERINGBOBAT";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("BOULDERINGBOBAT"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: promocode made of all numbers is still matched */
    @Test
    public void codeNoQuotes_promocodeAllNumbers() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code 12345";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("12345"), equalTo(actual));
    }

    /** CODE_NO_QUOTES: String must have standalone 'code' word */
    @Test
    public void codeNoQuotes_standaloneKeyword() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using Decode BOULDERINGBOBAT";

        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    } 

    /** CODE_NO_QUOTES: multiple matches in 1 String */
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
        assertThat(expected, equalTo(actual));
    }


    /*                                                                   *
     * ==================== TEST 'CODE' WITH QUOTES ==================== *
     *                        ex. "code "OFF20""                         */

    private static final Pattern CODE_WITH_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_WITH_QUOTES.getPattern();

    /** CODE_WITH_QUOTES_PATTERN: letters (both upper and lowercase), numbers, and symbols */
    @Test
    public void codeWithQuotes_lettersNumbersSymbols() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"CooL1R-PA!\".";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("CooL1R-PA!"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: keyword case insensitive */
    @Test
    public void codeWithQuotes_keywordCaseInsensitive() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"COOLIRPA\".";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("COOLIRPA"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: 0 characters inside quotes not matched */
    @Test
    public void codeWithQuotes_0CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"\". ";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: 1 character inside quotes is matched */
    @Test
    public void codeWithQuotes_1CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"C\". ";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("C"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: keyword at start of description */
    @Test
    public void codeWithQuotes_keywordAtStart() {
        String desc = "Code \"COOLIRPA\" and save 33% on your first Native Deodorant Pack - "
            + "normally $36, you'll get it for $24! Click here: https://bit.ly/nativecoolirpa.";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("COOLIRPA"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: keyword at end of description */
    @Test
    public void codeWithQuotes_keywordAtEnd() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat15"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: 2 spaces between keyword and quotes is not matched */
    @Test
    public void codeWithQuotes_2Spaces() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code  \"bobat15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: a line break between the keyword and code is acceptable */
    @Test
    public void codeWithQuotes_newLineBetween() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code\n\"bobat15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat15"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: a line break inside the set of quotes will invalidate match */
    @Test
    public void codeWithQuotes_newLineInsideQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"boba\nt15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: keyword must come before the code, not after */
    @Test
    public void codeWithQuotes_lookbehind() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer \"bobat15\" code";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: keyword cannot be embedded inside another word */
    @Test
    public void codeWithQuotes_standaloneKeyword() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer decode \"bobat15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Collections.emptyList(), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: end match at first set of quotes encountered (lazy) */
    @Test
    public void codeWithQuotes_lazyMatching() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat\"15\"";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: also allow single quotes */
    @Test
    public void codeWithQuotes_singleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code 'bobat15'";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat15"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: also allow a mix of both quotes */
    @Test
    public void codeWithQuotes_singleAndDoubleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat15'";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat15"), equalTo(actual));
    }

    /** CODE_WITH_QUOTES_PATTERN: 2 matches found */
    @Test
    public void codeWithQuotes_multipleMatches() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code 'bobat15'"
            + "Save 33% on your first Native Deodorant Pack - normally $36, you'll get it for "
            + "$24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\".";

        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(Arrays.asList("bobat15", "COOLIRPA"), equalTo(actual));
    }

}

