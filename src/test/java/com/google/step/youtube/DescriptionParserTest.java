package com.google.step.youtube;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DescriptionParserTest {

    private static final Pattern CODE_NO_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_NO_QUOTES.getPattern();
    private static final Pattern CODE_WITH_QUOTES_PATTERN = DescriptionParser.Patterns.CODE_WITH_QUOTES.getPattern();
    private static final Pattern TO_AT_LINKS_PATTERN = DescriptionParser.Patterns.TO_AT_LINKS.getPattern();
    private static final Pattern SYMBOL_NEAR_LINK_PATTERN = DescriptionParser.Patterns.SYMBOL_NEAR_LINK.getPattern();

    private static final String COMPANY_NAME = "COMPANY_NAME";

    /*
     * * ================== TESTS FOR PARSE BY COMPANY =================== *
     */

    @Test
    public void parseByCompany_0CompanyName1Promocode() {
        String desc = "Get 20% OFF + Free International Shipping instantly at http://Manscaped.com/phil";
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(Collections.emptyList()));
    }

    @Test
    public void parseByCompany_1CompanyName0Promocodes() {
        String desc = "And if you want to order food through " + COMPANY_NAME + " use my code";
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(Collections.emptyList()));
    }

    @Test
    public void parseByCompany_1CompanyName1Promocode() {
        String desc = "And if you want to order food through " + COMPANY_NAME + " use my code A1JZN";
        List<OfferSnippet> expected = Arrays.asList(OfferSnippet.create("A1JZN", desc));
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(expected));
    }

    @Test
    public void parseByCompany_1CompanyName2Promocodes() {
        String desc = "Order food through " + COMPANY_NAME + " go to https://pmfleet.app.link/zyoaw9s3R6, "
                + "use my code A1JZN";
        List<OfferSnippet> expected = Arrays.asList(
                                    OfferSnippet.create("A1JZN", desc),
                                    OfferSnippet.create("https://pmfleet.app.link/zyoaw9s3R6", desc));
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(expected));
    }

    @Test
    public void parseByCompany_2CompanyNames1Promocode() {
        String snippet = "DISCOUNT!!! For a limited time only, take 25% off your first purchase at "
                + COMPANY_NAME + "with the code OVDOINGTHINGS25. This offer is valid online only. ";
        String desc = snippet + "\nMust apply code at " + COMPANY_NAME + " checkout. Expires August 31, 2020.";
        List<OfferSnippet> expected = Arrays.asList(OfferSnippet.create("OVDOINGTHINGS25", snippet));
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(expected));
    }

    @Test
    public void parseByCompany_2CompanyNames2Promocodes() {
        String snippet1 = "DOWNLOAD (affiliate link): Go to https://" + COMPANY_NAME + ".com/pewdiepie";
        String snippet2 = "and use code PEWDIEPIE to get 70% off a 3 year plan of" + COMPANY_NAME + ".";
        String desc = snippet1 + "\n" + snippet2;
        List<OfferSnippet> expected = Arrays.asList(
                                    OfferSnippet.create("https://" + COMPANY_NAME + ".com/pewdiepie", snippet1), 
                                    OfferSnippet.create("PEWDIEPIE", snippet2));
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(expected));
    }

    @Test
    public void parseByCompany_companyNameCaseSensitive() {
        String desc = "Go to " + COMPANY_NAME.toLowerCase() + " and use code 20OFF";
        List<OfferSnippet> expected = Arrays.asList(OfferSnippet.create("20OFF", desc));
        assertThat(DescriptionParser.parseByCompany(COMPANY_NAME, desc), equalTo(expected));
    }


    /*
     * * ======================== TESTS FOR PARSE ======================== *
     */

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
        String descFUNGBROS10 = "Use code FUNGBROS10. ";
        String descCOOLIRPA = "Click here and use my code \"COOLIRPA\".";
        String descPEWDIEPIE = "Go to https://NordVPN.com/pewdiepie and use code PEWDIEPIE ";
        String descGlassware = "use https://lmg.gg/glasswire to get 25% off";
        String descBokksu = "Get 10% off your own authentic Japanese snack box from Bokksu using my link: https://bit.ly/3fYbkZ5";
        String desc = descFUNGBROS10 + "\n\n" + descCOOLIRPA + "\n\n" + descPEWDIEPIE + "\n\n" 
                + descGlassware + "\n\n" + descBokksu;

        OfferSnippet expectedFUNGBROS10 = OfferSnippet.create("FUNGBROS10", descFUNGBROS10);
        OfferSnippet expectedPEWDIEPIE = OfferSnippet.create("PEWDIEPIE", descPEWDIEPIE);
        OfferSnippet expectedCOOLIRPA = OfferSnippet.create("COOLIRPA", descCOOLIRPA);
        OfferSnippet expectedNordVPN = OfferSnippet.create("https://NordVPN.com/pewdiepie", descPEWDIEPIE);
        OfferSnippet expectedGlassware = OfferSnippet.create("https://lmg.gg/glasswire", descGlassware);
        OfferSnippet expectedBokksu = OfferSnippet.create("https://bit.ly/3fYbkZ5", descBokksu);

        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(expectedGlassware, 
        expectedCOOLIRPA, expectedPEWDIEPIE, expectedNordVPN, expectedBokksu, expectedFUNGBROS10)));
    }

    @Test
    public void parse_extraLineAfter() {
        String snippetFUNGBROS10 = "Use code FUNGBROS10 ";
        String desc = snippetFUNGBROS10 + "\nRyan: https://www.instagram.com/ryan.w.benson/";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                                            OfferSnippet.create("FUNGBROS10", snippetFUNGBROS10))));
    }

    @Test
    public void parse_oneLineDescription() {
        String desc = "Click here and use my code \"COOLIRPA\". ";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(OfferSnippet.create("COOLIRPA", desc))));
    }

    @Test
    public void parse_extraLineBefore() {
        String snippetNordVPN = "Go to https://NordVPN.com/pewdiepie and get 70 percent off";
        String desc = "ty for 10 years of pewdiepie youtube uploads brvus\n" + snippetNordVPN + "\n";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                            OfferSnippet.create("https://NordVPN.com/pewdiepie", snippetNordVPN))));
    }

    @Test
    public void parse_repeatedPromoCodesDifferentSnippets() {
        String firstCOOLIRPA = "Also my code \"COOLIRPA\"";
        String secondCOOLIRPA = "Use my code \"COOLIRPA\".";
        String desc = firstCOOLIRPA + "\n" + secondCOOLIRPA + "\n";

        OfferSnippet firstExpected = OfferSnippet.create("COOLIRPA", firstCOOLIRPA);
        OfferSnippet secondExpected = OfferSnippet.create("COOLIRPA", secondCOOLIRPA);
        
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(firstExpected, secondExpected)));
    }

    @Test
    public void parse_snippetTruncateBothEnds() {
        String truncatedSnippet = "App store). This episode originally recorded June 8, 2020, and "
                + "is sponsored by Stamps.com (Go to http://stamps.com, click on the microphone "
                + "at the top of the homepage, and type in ROOSTER to claim";
        String desc = "Mercari (Buy or sell almost anything on Mercari on the " 
                + truncatedSnippet + " your special offer).";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                    OfferSnippet.create("http://stamps.com", "... " + truncatedSnippet + " ..."))));
    }

    @Test
    public void parse_snippetTruncateBeginning() {
        String truncatedSnippet = "App store). This episode originally recorded June 8, 2020, and "
                + "is sponsored by Stamps.com (Go to http://stamps.com, click on the microphone "
                + "at the top of the homepage, and type in ROOSTER to claim";
        String desc = "Mercari (Buy or sell almost anything on Mercari on the " + truncatedSnippet;
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                    OfferSnippet.create("http://stamps.com", "... " + truncatedSnippet))));
    }

    @Test
    public void parse_snippetTruncateEnd() {
        String truncatedSnippet = "App store). This episode originally recorded June 8, 2020, and "
                + "is sponsored by Stamps.com (Go to http://stamps.com, click on the microphone "
                + "at the top of the homepage, and type in ROOSTER to claim";
        String desc = truncatedSnippet + " your special offer).";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                OfferSnippet.create("http://stamps.com", truncatedSnippet + " ..."))));
    }

    @Test
    public void parse_snippetStopAtDelimiter() {
        String truncatedSnippet = "App store). This episode originally recorded June 8, 2020, and "
                + "is sponsored by Stamps.com (Go to http://stamps.com, click on the microphone ";
        String desc = truncatedSnippet + "\nat the top of the homepage, and type in ROOSTER to claim";
        assertThat(DescriptionParser.parse(desc), equalTo(Arrays.asList(
                    OfferSnippet.create("http://stamps.com", truncatedSnippet))));
    }


    /*
     * * ===================== TEST 'CODE' NO QUOTES ===================== * 
     * ex. "code OFF20"
     */

    @Test
    public void codeNoQuotes_lettersNumbersHyphen() {
        String desc = "Get 10% off (save up to $44!) your own authentic Japanese snack box from "
                + "Bokksu using my link: https://bit.ly/3fYbkZ5 and code FUNGBROS-10";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("FUNGBROS-10")));
    }

    @Test
    public void codeNoQuotes_promocodeCaseSensitive() {
        String desc = "Use code LinusssS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("LinusssS")));
    }

    @Test
    public void codeNoQuotes_keywordCaseInsensitive() {
        String desc = "Use cOdE LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("LINUS")));
    }

    @Test
    public void codeNoQuotes_1ChararcterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code R at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_2CharacterPromocode() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code RO at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("RO")));
    }

    @Test
    public void codeNoQuotes_ColonSpace() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code: ROOSTER at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("ROOSTER")));
    }

    @Test
    public void codeNoQuotes_3Spaces() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code   ROOSTER at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_newLine() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code\nROOSTER at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("ROOSTER")));
    }

    @Test
    public void codeNoQuotes_symbols() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code ROOSTER! at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("ROOSTER")));
    }

    @Test
    public void codeNoQuotes_keywordAtStart() {
        String desc = "code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("LINUS")));
    }

    @Test
    public void codeNoQuotes_keywordAtEnd() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
                + "first purchase of a website or domain using code BOULDERINGBOBAT";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("BOULDERINGBOBAT")));
    }

    @Test
    public void codeNoQuotes_promocodeAllNumbers() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
                + "first purchase of a website or domain using code 12345";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("12345")));
    }

    @Test
    public void codeNoQuotes_standaloneKeyword() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
                + "first purchase of a website or domain using Decode BOULDERINGBOBAT";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeNoQuotes_multipleMatches() {
        String desc = "This episode originally recorded June 8, 2020, and is sponsored by Stamps.com "
                + "(Go to http://stamps.com, click on the microphone at the top of the homepage, "
                + "and type in code ROOSTERTEETH to claim your special offer), Mercari "
                + "(Buy or sell almost anything on Mercari on the App store or at http://mercari.com), "
                + "and Bespoke Post (Get 20% off your first monthly box when you sign up at "
                + "http://boxofawesome.com and enter the code ROOSTER at checkout!).";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_NO_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("ROOSTERTEETH", "ROOSTER")));
    }

    /*
     * * ==================== TEST 'CODE' WITH QUOTES ==================== * 
     * ex. "code "OFF20""
     */

    @Test
    public void codeWithQuotes_lettersNumbersSymbols() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get "
                + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my code \"CooL1R-PA!\".";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("CooL1R-PA!")));
    }

    @Test
    public void codeWithQuotes_keywordCaseInsensitive() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get "
                + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"COOLIRPA\".";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("COOLIRPA")));
    }

    @Test
    public void codeWithQuotes_0CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get "
                + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"\". ";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_1CharacterPromocode() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally $36, you'll get "
                + "it for $24! Click here: https://bit.ly/nativecoolirpa and use my coDe \"C\". ";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("C")));
    }

    @Test
    public void codeWithQuotes_keywordAtStart() {
        String desc = "Code \"COOLIRPA\" and save 33% on your first Native Deodorant Pack - "
                + "normally $36, you'll get it for $24! Click here: https://bit.ly/nativecoolirpa.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("COOLIRPA")));
    }

    @Test
    public void codeWithQuotes_keywordAtEnd() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code \"bobat15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_ColonSpace() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code: \"bobat15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_3Spaces() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code   \"bobat15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_newLineBetween() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code\n\"bobat15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_newLineInsideQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code \"boba\nt15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_lookbehind() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer \"bobat15\" code";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void codeWithQuotes_standaloneKeyword() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer decode \"bobat15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    // lazy matching: end match at first set of quotes encountered
    @Test
    public void codeWithQuotes_lazyMatching() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code \"bobat\"15\"";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat")));
    }

    @Test
    public void codeWithQuotes_singleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code 'bobat15'";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_singleAndDoubleQuotes() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code \"bobat15'";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15")));
    }

    @Test
    public void codeWithQuotes_multipleMatches() {
        String desc = "Get %15 off Rhino Skin, Unparallel shoes and much more at "
                + "https://darkventures.co.uk/shop with offer code 'bobat15'"
                + "Save 33% on your first Native Deodorant Pack - normally $36, you'll get it for "
                + "$24! Click here: https://bit.ly/nativecoolirpa and use my code \"COOLIRPA\".";
        List<OfferSnippet> actual = DescriptionParser.findMatches(CODE_WITH_QUOTES_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("bobat15", "COOLIRPA")));
    }

    /*
     * * ============= TEST 'TO' AND 'AT' FOR AFFILATE LINKS ============= * 
     * ex. "go to https://google.com" | "50% off at https://website.com"
     */

    @Test
    public void toAtLinks_lettersNumbersSymbols() {
        String desc = "Go to https://NordVPN.com/123!pewdiepie-test and use code PEWDIEPIE to "
                + "get 70% off a 3 year plan plus 1 additional month free.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://NordVPN.com/123!pewdiepie-test")));
    }

    @Test
    public void toAtLinks_toCaseInsensitive() {
        String desc = "Go tO https://NordVPN.com/pewdiepie and use code PEWDIEPIE to "
                + "get 70% off a 3 year plan plus 1 additional month free.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://NordVPN.com/pewdiepie")));
    }

    @Test
    public void toAtLinks_atCaseInsensitive() {
        String desc = "Get 20% off your first monthly box when you sign up AT http://boxofawesome.com";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://boxofawesome.com")));
    }

    @Test
    public void toAtLinks_0CharacterLinkBody() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http:// and enter the code ROOSTER at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_1CharacterLinkBody() {
        String desc = "Get 20% off your first monthly box when you sign up at "
                + "http://a and enter the code ROOSTER at checkout!";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://a")));
    }

    @Test
    public void toAtLinks_multipleMatchesHttpsAndHttp() {
        String desc = "Go to https://NordVPN.com/pewdiepie and use code PEWDIEPIE to get 70% "
                + "off a 3 year plan plus 1 additional month free."
                + "Go to http://stamps.com, click on the microphone at the top of the homepage";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://NordVPN.com/pewdiepie", "http://stamps.com")));
    }

    @Test
    public void toAtLinks_noHttpOnlyWWW() {
        String desc = "Go to www.NordVPN.com/pewdiepie and use code PEWDIEPIE to get 70% "
                + "off a 3 year plan plus 1 additional month free.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_lookbehind() {
        String desc = "Use code LINUS and get 25% off GlassWire https://lmg.gg/glasswire at";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_standaloneAt() {
        String desc = "Use code LINUS and get 25% off GlassWire what https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_standaloneTo() {
        String desc = "Use code LINUS and get 25% off GlassWire burrito https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_keywordAtStart() {
        String desc = "At https://lmg.gg/glasswire use code LINUS and get 25% off GlassWire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_keywordAtEnd() {
        String desc = "Use code LINUS and get 25% off GlassWire at https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_ColonSpace() {
        String desc = "Use code LINUS and get 25% off GlassWire at: https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_3Spaces() {
        String desc = "Use code LINUS and get 25% off GlassWire at   https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test
    public void toAtLinks_newLineBetween() {
        String desc = "Use code LINUS and get 25% off GlassWire at\nhttps://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://lmg.gg/glasswire")));
    }

    @Test
    public void toAtLinks_stopMatchingAtSpace() {
        String desc = "Head to https://www.squarespace.com/boulderin... to save 10% off your "
                + "first purchase of a website or domain using code BOULDERINGBOBAT. ";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://www.squarespace.com/boulderin...")));
    }

    @Test
    public void toAtLinks_stopMatchingAtNewline() {
        String desc = "Head to https://www.squarespace.com/boulderin...\nto save 10% off your "
                + "first purchase of a website or domain using code BOULDERINGBOBAT. ";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://www.squarespace.com/boulderin...")));
    }

    @Test
    public void toAtLinks_stopMatchingAtComma() {
        String desc = "Go to http://stamps.com, click on the microphone";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://stamps.com")));
    }

    @Test
    public void toAtLinks_stopMatchingAtCloseParens() {
        String desc = "Buy or sell almost anything on Mercari on the App store or at http://mercari.com)";
        List<OfferSnippet> actual = DescriptionParser.findMatches(TO_AT_LINKS_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://mercari.com")));
    }


    /*
     * * ================ TEST $ OR % SYMBOLS BEFORE LINK ================ * 
     * ex. "get 50% off: https://google.com" | "get $20 off: https://website.com"
     */

    @Test 
    public void symbolBeforeLink_regualarMatchHttpPercent() {
        String desc = "Save 33% on your first Native Deodorant Pack. Click here: http://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_regualarMatchHttpDollarSign() {
        String desc = "Save $20 on your first Native Deodorant Pack. Click here: http://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_noNumberBeforePercent() {
        String desc = "Save % on your first Native Deodorant Pack. Click here: https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolBeforeLink_noNumberAfterDollar() {
        String desc = "Save $ on your first Native Deodorant Pack. Click here: https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolBeforeLink_0CharsBetween() {
        String desc = "Save $3https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolBeforeLink_1CharBetween() {
        String desc = "Save $3 https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_moreThan100CharsBetween() {
        String desc = "Save 33% on your first Native Deodorant Pack - normally 36, you'll get "
                + "it for 24! Filler sentence here. Click here: https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolBeforeLink_newLineBetween() {
        String desc = "Save $3 \n https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolBeforeLink_stopMatchingAtComma() {
        String desc = "Save $30 https://bit.ly/nativecoolirpa,";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_stopMatchingAtCloseParens() {
        String desc = "Save $30 https://bit.ly/nativecoolirpa)";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_keywordAtStart() {
        String desc = "$30 https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_keywordAtEnd() {
        String desc = "hello $30 https://bit.ly/nativecoolirpa";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolBeforeLink_multipleMatches() {
        String desc = "hello $30: https://bit.ly/nativecoolirpa or 20% off at: https://lmg.gg/glasswire";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa", 
                                                                    "https://lmg.gg/glasswire")));
    }


    /*
     * * ================ TEST $ OR % SYMBOLS AFTER LINK ================ * 
     * ex. "use https://google.com for 20% off" | "use https://website.com: $30 off"
     */

    @Test 
    public void symbolAfterLink_regualarMatchHttpPercent() {
        String desc = "Click here: http://bit.ly/nativecoolirpa and save 33% on your first Native Deodorant Pack.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_regualarMatchHttpDollarSign() {
        String desc = "Click here: http://bit.ly/nativecoolirpa to save $20 on your first Native Deodorant Pack.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("http://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_noNumberBeforePercent() {
        String desc = "Click here: https://bit.ly/nativecoolirpa and save % on your first Native Deodorant Pack.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolAfterLink_noNumberAfterDollar() {
        String desc = "Click here: https://bit.ly/nativecoolirpa and save $ on your first Native Deodorant Pack.";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolAfterLink_0CharsBetween() {
        String desc = "https://$3";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolAfterLink_1CharBetween() {
        String desc = "Save https://w $3";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://w")));
    }

    @Test 
    public void symbolAfterLink_moreThan100CharsBetween() {
        String desc = "Click here: https://bit.ly/nativecoolirpa and save on your first Native "
                + "Deodorant Pack - normally 36, you'll get it for 24! Filler sentence here. Save $3";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolAfterLink_newLineBetween() {
        String desc = "Save https://bit.ly/nativecoolirpa \n 30%";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Collections.emptyList()));
    }

    @Test 
    public void symbolAfterLink_stopMatchingAtComma() {
        String desc = "Save https://bit.ly/nativecoolirpa, $30";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_stopMatchingAtCloseParens() {
        String desc = "Save (https://bit.ly/nativecoolirpa) 40%";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_keywordAtStart() {
        String desc = "https://bit.ly/nativecoolirpa filler text $30 filler text";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_keywordAtEnd() {
        String desc = "filler text https://bit.ly/nativecoolirpa filler text $30";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa")));
    }

    @Test 
    public void symbolAfterLink_multipleMatches() {
        String desc = "use: https://bit.ly/nativecoolirpa for $5 off or go to: https://lmg.gg/glasswire for 20% off";
        List<OfferSnippet> actual = DescriptionParser.findMatches(SYMBOL_NEAR_LINK_PATTERN, desc);
        assertThat(extractPromoCodes(actual), equalTo(Arrays.asList("https://bit.ly/nativecoolirpa", 
                                                                    "https://lmg.gg/glasswire")));
    }


    /* helper method for findMatches() tests, only checking for promocode field */
    private List<String> extractPromoCodes(List<OfferSnippet> offerSnippets) {
        return offerSnippets.stream().map(OfferSnippet::getPromoCode).collect(ImmutableList.toImmutableList());
    } 

}
