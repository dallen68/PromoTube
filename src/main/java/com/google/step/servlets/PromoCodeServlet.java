package com.google.step.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/promo-code")
public class PromoCodeServlet extends HttpServlet {
    
    @Override
    public void init() {
        YoutubeInfoScraper InfoScraper = new YoutubeInfoScraper();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String channelId = request.getParameter("formInput");
        InfoScraper.scrapeChannelUploadPlaylist(channelId);
    }
}
