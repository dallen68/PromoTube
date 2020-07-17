package com.google.step.servlets;

import com.google.step.youtube.YouTubeInfoScraper;
import com.google.step.youtube.PromoCode;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/promo-code")
public class PromoCodeServlet extends HttpServlet {

    private YouTubeInfoScraper infoScraper;

    @VisibleForTesting
    static final String REQUEST_PARAMETER = "formInput";

    public PromoCodeServlet(YouTubeInfoScraper infoScraper) {
        this.infoScraper = infoScraper;
    }

    public PromoCodeServlet() {
        init();
    }

    @Override
    public void init() {
        infoScraper = new YouTubeInfoScraper();
    }

    /**
     * Takes in formInput as a parameter and then checks for a promoCodes based on
     * the userInput. If the userInput is real and there are codes the json will
     * return a list of promoCodes. If the userInput is not proper then the json
     * will return an empty list. In a case where an exception is thrown the json
     * will return an empty list.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter(REQUEST_PARAMETER);
        response.setContentType("application/json");

        try {
            Optional<String> playlistId;
            if (userInput.startsWith("https://www.youtube.com/channel/")) {
                playlistId = infoScraper.scrapeChannelUploadPlaylist(userInput.split("/")[4]);
            } else if (userInput.startsWith("https://www.youtube.com/user/")) {
                playlistId = infoScraper.scrapeUserUploadPlaylist(userInput.split("/")[4]);
            } else {
                response.getWriter().println(new Gson().toJson(ImmutableList.of()));
                return;
            }

            if (!playlistId.isPresent()) {
                response.getWriter().println(new Gson().toJson(ImmutableList.of()));
                return;
            }
            Optional<List<PromoCode>> promoCodeList = infoScraper.scrapePromoCodesFromPlaylist(playlistId.get());
            response.getWriter().println(new Gson().toJson(promoCodeList.orElse(ImmutableList.of())));
        } catch (IOException exception) {
            response.getWriter().println(new Gson().toJson(ImmutableList.of()));
        }
    }
}
