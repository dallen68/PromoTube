package com.google.step.servlets;

import com.google.step.youtube.YoutubeInfoScraper;
import com.google.step.youtube.PromoCode;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
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

    private YoutubeInfoScraper infoScraper;

    public PromoCodeServlet(YoutubeInfoScraper infoScraper) {
        this.infoScraper = infoScraper;
    }
    public PromoCodeServlet() {
        init();
    }

    @Override
    public void init() {
        infoScraper = new YoutubeInfoScraper();
    }
    /**
     * Takes in formInput as a parameter and then checks for a promoCodes based on the channelId.
     * If the channelId is real and there are codes the json will return a list of promoCodes.
     * If the channelId is not proper then the json will return an empty list.
     * In a case where an exception is thrown the json will return an empty list.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String channelId = request.getParameter("formInput");
        response.setContentType("application/json");
        try {
            Optional<String> playlistId = infoScraper.scrapeChannelUploadPlaylist(channelId);
            if (playlistId.isPresent()) {
                Optional<List<PromoCode>> promoCodeList = infoScraper.scrapePromoCodesFromPlaylist(playlistId.get());
                if (promoCodeList.isPresent()) {
                    response.getWriter().println(new Gson().toJson(promoCodeList.get()));
                } else {
                    response.getWriter().println(new Gson().toJson(ImmutableList.of()));
                }
            } else {
                response.getWriter().println(new Gson().toJson(ImmutableList.of()));
            }
        } catch (IOException exception) {
            response.getWriter().println(new Gson().toJson(ImmutableList.of()));
        }

    }
}
