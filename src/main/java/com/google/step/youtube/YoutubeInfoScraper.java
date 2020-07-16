
package com.google.step.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Scrapes a channel's upload playlist and scrapes the channel's videos +
 * descriptions
 */
public class YoutubeInfoScraper {

    // TODO: Add seperate file to hold API Key
    private static final String API_KEY = "";
    private static final String APPLICATION_NAME = "promotube";

    private final YouTube youTubeClient;

    public YoutubeInfoScraper(YouTube youTubeClient) {
        this.youTubeClient = youTubeClient;
    }

    public YoutubeInfoScraper() {
        this(new YouTube.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                /* httpRequestInitializer= */ null).setApplicationName(APPLICATION_NAME)
                        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY)).build());
    }

    /**
     * @param channelId Id of a channel. Can be found in channel's urls. e.g.
     *                  https://www.youtube.com/channel/<CHANNEL-ID>
     * @return an optional string of the channel's upload playlist id. The optional
     *         will be empty if id is invalid or no items were found.
     */
    public Optional<String> scrapeChannelUploadPlaylist(String channelId) throws IOException {
        ChannelListResponse response = youTubeClient.channels().list("contentDetails").setId(channelId).execute();
        // getItems() return null when no items match the criteria (channelId).
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }
        List<Channel> channelsInfo = response.getItems();
        // Since we are only requesting one channel-id, we only get one item back.
        return Optional.of(channelsInfo.get(0).getContentDetails().getRelatedPlaylists().getUploads());
    }

    /**
     * @param uploadId Id of a channel's upload playlist.
     * @return an optional list of PromoCode objects. The optional will return empty
     *         if id is invalid or no items in the playlist are found.
     */
    public Optional<List<PromoCode>> scrapePromoCodesFromPlaylist(String uploadId) throws IOException {
        Optional<List<PlaylistItem>> playlistItems = scrapePlaylistItems(uploadId);
        if (playlistItems.isEmpty()) {
            return Optional.empty();
        }
        List<PromoCode> promoCodes = new ArrayList<>();
        for (PlaylistItem item : playlistItems.get()) {
            PlaylistItemSnippet snippet = item.getSnippet();
            List<OfferSnippet> itemOfferSnippets = DescriptionParser.parse(snippet.getDescription());
            for (OfferSnippet offer : itemOfferSnippets) {
                promoCodes.add(
                        PromoCode.create(offer.getPromoCode(), offer.getSnippet(), snippet.getResourceId().getVideoId(),
                                snippet.getTitle(), new Date(snippet.getPublishedAt().getValue())));
            }
        }
        return Optional.of(promoCodes);
    }

    /**
     * @param uploadId Id of a channel's upload playlist.
     * @return an optional list of PlaylistItems. Each item contains a video-id,
     *         description and a date. The optional will be empty if id is invalid
     *         or no items were found.
     */
    public Optional<List<PlaylistItem>> scrapePlaylistItems(String uploadId) throws IOException {
        PlaylistItemListResponse response = youTubeClient.playlistItems().list("snippet").setMaxResults(50L)
                .setPlaylistId(uploadId).execute();
        // getItems() return null when no items match the criteria (uploadId).
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.getItems());
    }

    /**
     * @param keyword Word to search with.
     * @return an optional list of PlaylistItems. Each item contains a video-id,
     *         description and a date. The optional will be empty if id is invalid
     *         or no items were found.
     */
    public Optional<List<String>> scrapeVideoIdsFromSearch(String keyword) throws IOException {
        SearchListResponse response = youTubeClient.search().list("").setMaxResults(50L).setQ(keyword)
                .setFields("items(id)").execute();
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }
        List<String> videoIds = new ArrayList<>();
        for (SearchResult result : response.getItems()) {
            videoIds.add(result.getId().getVideoId());
        }
        return Optional.of(videoIds);
    }
}
