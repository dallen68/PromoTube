package com.google.step.youtube;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DescriptionParserTest {

    private static final Pattern CODE_NO_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_NO_QUOTES.getPattern();
    private static final Pattern CODE_WITH_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_WITH_QUOTES.getPattern();
    private static final Pattern TO_AT_LINKS_PATTERN = DescriptionParser.Patterns.TO_AT_LINKS.getPattern();

    /*                                                                   *
     * ==================== GENERAL TESTS FOR PARSE ==================== *
     *                                                                   */

    @Test
    public void parse_matchesFromNone() {
        String desc = "Check out our UPDATED version which has all the NEW ELEMENTS here:  "
            + "https://youtu.be/rz4Dd1I_fX0"
            + "\nThe TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
            + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";
        assertThat(DescriptionParser.parse(desc), equalTo(Collections.emptyList()));
    }

    @Test
    public void parse_matchesFromAll() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10. "
            + "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\"."
            + "Go to https://NordVPN.com/pewdiepie and use code PEWDIEPIE to "
            + "get 70% off a 3 year plan plus 1 additional month free.";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList("FUNGBROS10", "PEWDIEPIE", 
                                                    "COOLIRPA", "https://NordVPN.com/pewdiepie")));
    }

    @Test
    public void parse_codeNoQuotes() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList("FUNGBROS10")));
    }

    @Test
    public void parse_codeWithQuotes() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
        + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\". ";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList("COOLIRPA")));
    }

    @Test
    public void parse_toAtLink() {
        String desc = "Go to https://NordVPN.com/pewdiepie and"
            + "get 70% off a 3 year plan plus 1 additional month free.";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList("https://NordVPN.com/pewdiepie")));
    }


    /*                                                                   *
     * ===================== TEST 'CODE' NO QUOTES ===================== *
     *                         ex. "code OFF20"                          */

    @Test
    public void codeNoQuotes_lettersAndNumbers() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
            + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS10";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("FUNGBROS10")));
    }

    @Test
    public void codeNoQuotes_promocodeCaseSensitive() {
        String desc = "Use code LINUSsssS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("LINUS")));
    }

    @Test
    public void codeNoQuotes_keywordCaseInsensitive() {
        String desc = "Use cOdE LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("LINUS")));
    }

    @Test
    public void codeNoQuotes_1ChararcterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code R at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_2CharacterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code RO at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("RO")));
    }

    @Test
    public void codeNoQuotes_lookbehind() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the ROOSTER code at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_2Spaces() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code  ROOSTER at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_newLine() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code\nROOSTER at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("ROOSTER")));
    }

    @Test
    public void codeNoQuotes_symbols() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://boxofawesome.com and enter the code ROOSTER! at checkout!";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("ROOSTER")));
    } 

    @Test
    public void codeNoQuotes_keywordAtStart() {
        String desc = "code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("LINUS")));
    }

    @Test
    public void codeNoQuotes_keywordAtEnd() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code BOULDERINGBOBAT";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("BOULDERINGBOBAT")));
    }

    @Test
    public void codeNoQuotes_promocodeAllNumbers() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code 12345";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("12345")));
    }

    @Test
    public void codeNoQuotes_standaloneKeyword() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using Decode BOULDERINGBOBAT";
        List<String> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    } 

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
        assertThat(actual, equalTo(expected));
    }


    /*                                                                   *
     * ==================== TEST 'CODE' WITH QUOTES ==================== *
     *                        ex. "code "OFF20""                         */

    @Test
    public void codeWithQuotes_lettersNumbersSymbols() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"CooL1R-PA!\".";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("CooL1R-PA!")));
    }

    @Test
    public void codeWithQuotes_keywordCaseInsensitive() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"COOLIRPA\".";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("COOLIRPA")));
    }

    @Test
    public void codeWithQuotes_0CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"\". ";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_1CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get " 
            + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"C\". ";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("C")));
    }

    @Test
    public void codeWithQuotes_keywordAtStart() {
        String desc = "Code \"COOLIRPA\" and save 33% on your first Native Deodorant Pack - "
            + "normally $36, you'll get it for $24! Click here: https://bit.ly/nativecoolirpa.";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("COOLIRPA")));
    }

    @Test
    public void codeWithQuotes_keywordAtEnd() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_2Spaces() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code  \"bobat15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_newLineBetween() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code\n\"bobat15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_newLineInsideQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"boba\nt15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_lookbehind() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer \"bobat15\" code";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_standaloneKeyword() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer decode \"bobat15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    // lazy matching: end match at first set of quotes encountered 
    @Test
    public void codeWithQuotes_lazyMatching() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat\"15\"";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat")));
    }

    @Test
    public void codeWithQuotes_singleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code 'bobat15'";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_singleAndDoubleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code \"bobat15'";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_multipleMatches() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
            + "https://darkventures.co.uk/shop with offer code 'bobat15'"
            + "Save 33% on your first Native Deodorant Pack - normally $36, you'll get it for "
            + "$24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\".";
        List<String> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("bobat15", "COOLIRPA")));
    }


    /*                                                                   *
     * ============= TEST 'TO' AND 'AT' FOR AFFILATE LINKS ============= *
     * ex. "go to https://google.com" | "50% off at https://website.com" */
    
    @Test
    public void toAtLinks_lettersNumbersSymbols() {
        String desc = "Go to https://NordVPN.com/123!pewdiepie-test and use code PEWDIEPIE to "
            + "get 70% off a 3 year plan plus 1 additional month free.";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://NordVPN.com/123!pewdiepie-test")));
    }

    @Test
    public void toAtLinks_toCaseInsensitive() {
        String desc = "Go tO https://NordVPN.com/pewdiepie and use code PEWDIEPIE to "
            + "get 70% off a 3 year plan plus 1 additional month free.";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://NordVPN.com/pewdiepie")));
    }

    @Test
    public void toAtLinks_atCaseInsensitive() {
        String desc = "Get 20% off your first monthly box when you sign up AT http://boxofawesome.com";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("http://boxofawesome.com")));
    }

    @Test
    public void toAtLinks_0CharacterLinkBody() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http:// and enter the code ROOSTER at checkout!";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_1CharacterLinkBody() {
        String desc = "Get 20% off your first monthly box when you sign up at "
            + "http://a and enter the code ROOSTER at checkout!";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("http://a")));
    }

    @Test
    public void toAtLinks_multipleMatchesHttpsAndHttp() {
        String desc = "Go to https://NordVPN.com/pewdiepie and use code PEWDIEPIE to get 70% "
            + "off a 3 year plan plus 1 additional month free."
            + "Go to http://stamps.com, click on the microphone at the top of the homepage";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://NordVPN.com/pewdiepie", "http://stamps.com")));
    }

    @Test
    public void toAtLinks_noHttpOnlyWWW() {
        String desc = "Go to www.NordVPN.com/pewdiepie and use code PEWDIEPIE to get 70% "
            + "off a 3 year plan plus 1 additional month free.";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }
    
    @Test
    public void toAtLinks_lookbehind() {
        String desc = "Use code LINUS and get 25% off GlassWire https://lmg.gg/glasswire at";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_standaloneAt() {
        String desc = "Use code LINUS and get 25% off GlassWire what https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_standaloneTo() {
        String desc = "Use code LINUS and get 25% off GlassWire burrito https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_keywordAtStart() {
        String desc = "At https://lmg.gg/glasswire use code LINUS and get 25% off GlassWire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_keywordAtEnd() {
        String desc = "Use code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_2SpacesBetween() {
        String desc = "Use code LINUS and get 25% off GlassWire at  https://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_newLineBetween() {
        String desc = "Use code LINUS and get 25% off GlassWire at\nhttps://lmg.gg/glasswire";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_stopMatchingAtSpace() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
            + "first purchase of a website or domain using code BOULDERINGBOBAT. ";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://www.squarespace.com/boulderin...")));
    }

    @Test
    public void toAtLinks_stopMatchingAtNewline() {
        String desc = "Head to https://www.squarespace.com/boulderin...\nto save 10% off your "
            + "first purchase of a website or domain using code BOULDERINGBOBAT. ";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("https://www.squarespace.com/boulderin...")));
    }

    @Test
    public void toAtLinks_stopMatchingAtComma() {
        String desc = "Go to http://stamps.com, click on the microphone";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("http://stamps.com")));
    }

    @Test
    public void toAtLinks_stopMatchingAtCloseParens() {
        String desc = "Buy or sell almost anything on Mercari on the App store or at http://mercari.com)";
        List<String> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(actual, equalTo(Arrays.asList("http://mercari.com")));
    }

}

