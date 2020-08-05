package com.google.step.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.step.youtube.PromoCode;
import com.google.step.youtube.YouTubeInfoScraper;

@WebServlet("/company/promo-codes")
public class CompanyPromoCodeServlet extends HttpServlet {

    private YouTubeInfoScraper infoScraper;

    @VisibleForTesting
    final String REQUEST_PARAMETER = "formInput";

    public CompanyPromoCodeServlet(YouTubeInfoScraper infoScraper) {
        this.infoScraper = infoScraper;
    }

    public CompanyPromoCodeServlet() {
        init();
    }

    @Override
    public void init() {
        infoScraper = new YouTubeInfoScraper();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter(REQUEST_PARAMETER);
        response.setContentType("application/json");

        try {
            Optional<List<String>> videoIds = infoScraper.scrapeVideoIdsFromSearch(userInput);
            if (!videoIds.isPresent()) {
                response.getWriter().println(new Gson().toJson(ImmutableList.of()));
                return;
            }
            Optional<List<PromoCode>> promoCodeList = infoScraper.scrapePromoCodesFromVideos(userInput, videoIds.get());
            response.getWriter().println(new Gson().toJson(promoCodeList.orElse(ImmutableList.of())));
        } catch (IOException exception) {
            response.getWriter().println(new Gson().toJson(ImmutableList.of()));
        }
    }

}
