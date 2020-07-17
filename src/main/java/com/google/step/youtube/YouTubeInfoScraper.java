
package com.google.step.youtube;

import static com.google.api.client.repackaged.com.google.common.base.Preconditions.checkState;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
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
public class YouTubeInfoScraper {

    // TODO: Add seperate file to hold API Key
    private static final String API_KEY = "";
    private static final String APPLICATION_NAME = "promotube";
    private final YouTube youTubeClient;

    public YouTubeInfoScraper(YouTube youTubeClient) {
        this.youTubeClient = youTubeClient;
    }

    public YouTubeInfoScraper() {
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
        return getYoutubeChannelResponse(response);
    }

    /**
     * @param userName username of a channel. Can be found in channel's urls. e.g.
     *                 https://www.youtube.com/user/<USER-NAME>
     * @return an optional string of the channel's upload playlist id. The optional
     *         will be empty if id is invalid or no items were found.
     */
    public Optional<String> scrapeUserUploadPlaylist(String userName) throws IOException {
        ChannelListResponse response = youTubeClient.channels().list("contentDetails").setForUsername(userName)
                .execute();
        return getYoutubeChannelResponse(response);
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
        if (response.getItems() == null) {
            return Optional.empty();
        }
        if (response.getItems().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.getItems());
    }

    /**
     * @param videoIds List of ids of youtube videos.
     * @return an optional list of Videos. The optional will be empty if id is
     *         invalid or no items were found.
     */
    public Optional<List<Video>> scrapeVideoInformation(List<String> videoIds) throws IOException {
        VideoListResponse response = youTubeClient.videos().list("snippet").setId(String.join(",", videoIds))
                .setFields("items(id, snippet(publishedAt, title, description))").execute();
        if (response.getItems() == null) {
            return Optional.empty();
        }
        checkState(!response.getItems().isEmpty(), "Expected more than 0 Videos to be found.");
        return Optional.of(response.getItems());
    }

    private Optional<String> getYoutubeChannelResponse(ChannelListResponse response) {
        // getItems() return null when no items match the criteria (channelId).
        if (response.getItems() == null) {
            return Optional.empty();
        }
        if (response.getItems().isEmpty()) {
            return Optional.empty();
        }
        checkState(response.getItems().size() == 1, "We should only be requesting a single channelId but got "
                + response.getItems().size() + " in response");
        return Optional.of(response.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads());
    }
}
