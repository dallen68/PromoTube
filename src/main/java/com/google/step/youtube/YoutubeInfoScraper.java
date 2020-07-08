
package com.google.step.youtube;

import java.util.ArrayList;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.util.Date;
import java.io.IOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.List;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.util.Optional;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

/**
 * Scrapes a channel's upload playlist and scrapes the channel's videos +
 * descriptions
 */
public class YoutubeInfoScraper {

    // TODO: Add seperate file to hold API Key
    private static final String API_KEY = "";
    private static final String APPLICATION_NAME = "promotube";

    private final YouTube youTubeClient;

    public YoutubeInfoScraper(final YouTube youTubeClient) {
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
    public Optional<String> scrapeChannelUploadPlaylist(final String channelId) throws IOException {
        final ChannelListResponse response = youTubeClient.channels().list("contentDetails").setId(channelId).execute();
        // getItems() return null when no items match the criteria (channelId).
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }
        final List<Channel> channelsInfo = response.getItems();
        // Since we are only requesting one channel-id, we only get one item back.
        return Optional.of(channelsInfo.get(0).getContentDetails().getRelatedPlaylists().getUploads());
    }

    /**
     * @param uploadId Id of a channel's upload playlist.
     * @return an optional list of PromoCode objects. the optional will return empty
     *         if id is invalid or no items in the playlist are found.
     */
    public Optional<List<PromoCode>> scrapePromoCodesFromPlaylist(final String uploadId) throws IOException {
        final Optional<List<PlaylistItem>> playlistItems = scrapePlaylistItems(uploadId);
        if (playlistItems.isEmpty()) {
            return Optional.empty();
        }
        final List<PromoCode> promoCodes = new ArrayList<>();
        for (final PlaylistItem item : playlistItems.get()) {
            final PlaylistItemSnippet snippet = item.getSnippet();
            final List<String> itemPromoCodes = DescriptionParser.parse(snippet.getDescription());
            for (final String promocode : itemPromoCodes) {
                promoCodes.add(PromoCode.create(promocode, snippet.getResourceId().getVideoId(),
                        new Date(snippet.getPublishedAt().getValue())));
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
    public Optional<List<PlaylistItem>> scrapePlaylistItems(final String uploadId) throws IOException {
        final PlaylistItemListResponse response = youTubeClient.playlistItems().list("snippet").setMaxResults(50L)
                .setPlaylistId(uploadId).execute();
        // getItems() return null when no items match the criteria (uploadId).
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.getItems());
    }
}
