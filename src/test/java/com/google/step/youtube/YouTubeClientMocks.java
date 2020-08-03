package com.google.step.youtube;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import java.util.Arrays;
import java.util.Date;

public class YouTubeClientMocks {

    private static final String UPLOAD_ID = "UPLOAD_ID";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final Date DATE = new Date(0L);

    public static final String KEYWORD = "KEYWORD";
    public static final String PROMOCODE = "PROMOCODE";
    public static final String PROMOCODE_DESCRIPTION = "Get 20% off your first monthly box and enter the code "
            + PROMOCODE;
    public static final String DESCRIPTION = "DESCRIPTION WITH NO PROMOCODE";
    public static final String KEYWORD_DESCRIPTION = "Get 25% off your first purchase at " + KEYWORD + " with the code "
            + PROMOCODE;

    public static PromoCode newPromoCode(String description) {
        return PromoCode.builder().setPromoCode(PROMOCODE).setSnippet(description).setVideoId(VIDEO_ID)
                .setVideoTitle(VIDEO_TITLE).setVideoUploadDate(DATE).build();
    }

    public static ChannelListResponse newBasicChannelResponse() {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        testChannelResponse.setItems(Arrays.asList(new Channel().setContentDetails(
                new ChannelContentDetails().setRelatedPlaylists(new RelatedPlaylists().setUploads(UPLOAD_ID)))));
        return testChannelResponse;
    }

    public static PlaylistItemListResponse newBasicPlaylistResponse() {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        testPlaylistResponse.setItems(Arrays.asList(newBasicPlaylistItem(PROMOCODE_DESCRIPTION)));
        return testPlaylistResponse;
    }

    public static PlaylistItem newBasicPlaylistItem(String description) {
        return new PlaylistItem()
                .setSnippet(new PlaylistItemSnippet().setDescription(description).setPublishedAt(new DateTime(DATE))
                        .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID)));
    }

    public static VideoListResponse newBasicVideoListResponse() {
        VideoListResponse testVideoResponse = new VideoListResponse();
        testVideoResponse.setItems(Arrays.asList(new Video(), new Video()));
        return testVideoResponse;
    }

    public static Video newBasicVideoResponse(String description) {
        return new Video().setId(VIDEO_ID).setSnippet(new VideoSnippet().setTitle(VIDEO_TITLE)
                .setDescription(description).setPublishedAt(new DateTime(DATE)));
    }

    public static SearchListResponse newBaSearchListResponse() {
        SearchListResponse testSearchResponse = new SearchListResponse();
        testSearchResponse.setItems(Arrays.asList(newBasicSearchResult(), newBasicSearchResult()));
        return testSearchResponse;
    }

    public static SearchResult newBasicSearchResult() {
        return new SearchResult().setId(new ResourceId().setVideoId(VIDEO_ID));
    }
}
