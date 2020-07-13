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

    public PromoCodeServlet(YoutubeInfoScraper infoScraper) {
        this.infoScraper = infoScraper;
    }
    
    @Override
    public void init() {
        infoScraper = new YoutubeInfoScraper();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String channelId = request.getParameter("formInput");
        try {
            Optional<String> playlistId = infoScraper.scrapeChannelUploadPlaylist(channelId);
            if (!playlistId.isPresent()) {
                response.setContentType("application/json");
                response.getWriter().println(new Gson().toJson(false));
            } else {
                Optional<List<PromoCode>> promoCodeList = infoScraper.scrapePromoCodesFromPlaylist(playlistId.get());
                response.setContentType("application/json");
                if (promoCodeList.isPresent()) {
                    response.getWriter().println(new Gson().toJson(promoCodeList.get()));
                } else {
                    response.getWriter().println(new Gson().toJson(false));
                }
                
            }
        } catch (IOException exception) {
            response.setContentType("application/json");
            response.getWriter().println(new Gson().toJson(false));
        }
        
    }
}
