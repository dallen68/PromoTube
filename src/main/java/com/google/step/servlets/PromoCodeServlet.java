package com.google.step.servlets;

import com.google.step.youtube.YoutubeInfoScraper;
import com.google.step.youtube.PromoCode;
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
    
    @Override
    public void init() {
        infoScraper = new YoutubeInfoScraper();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String channelId = request.getParameter("formInput");
        Optional<String> playlistID = infoScraper.scrapeChannelUploadPlaylist(channelId);
        if (!playlistID.isPresent()) {
             response.setContentType("application/json");
            String json = new Gson().toJson(false);
            response.getWriter().println(json);
        } else {
            Optional<List<PromoCode>> promoCodeList = infoScraper.scrapePromoCodesFromPlaylist(playlistID.get());
            response.setContentType("application/json");
            String json = new Gson().toJson(promoCodeList.get());
            response.getWriter().println(json);
        }
        
    }
}
