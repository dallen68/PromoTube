
package com.google.step.youtube;

import java.io.IOException;
import com.google.api.services.youtube.model.ChannelListResponse;

public interface YoutubeResponse{
    ChannelListResponse getYoutubeChannelListResponse(String channelId) throws IOException;
}
