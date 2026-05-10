package aiss_L3.TwitchMiner.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriUtils;

import aiss_L3.TwitchMiner.exception.TwitchApiException;
import aiss_L3.TwitchMiner.model.twitch.TwitchClip;
import aiss_L3.TwitchMiner.model.twitch.TwitchClipsResponse;
import aiss_L3.TwitchMiner.model.videominer.Comment;

/**
 * Service to fetch Twitch Clips from the Helix API and convert them to Comments.
 * Clips are treated as "comments" in the video detail response.
 */
@Service
public class TwitchClipsService {

    private static final int DEFAULT_CLIPS_LIMIT = 5;

    @Value("${twitch.base-url:https://api.twitch.tv/helix}")
    private String baseUrl;

    @Autowired
    private TwitchClient twitchClient;

    /**
     * Fetch clips for a specific video and map them to Comment objects.
     * 
     * @param broadcasterId The broadcaster's ID
     * @param videoId The video's ID
     * @param maxClips Maximum number of clips to retrieve (default: 5)
     * @return List of Comment objects representing clips
     */
    public List<Comment> getClipsAsComments(String broadcasterId, String videoId, Integer maxClips) {
        List<Comment> comments = new ArrayList<>();

        if (!StringUtils.hasText(broadcasterId) || !StringUtils.hasText(videoId)) {
            return comments;
        }

        int limit = (maxClips != null && maxClips > 0) ? maxClips : DEFAULT_CLIPS_LIMIT;

        String encodedBroadcasterId = UriUtils.encodeQueryParam(broadcasterId, StandardCharsets.UTF_8);
        String encodedVideoId = UriUtils.encodeQueryParam(videoId, StandardCharsets.UTF_8);
        String url = baseUrl + "/clips?broadcaster_id=" + encodedBroadcasterId
                + "&video_id=" + encodedVideoId
                + "&first=" + limit;

        try {
            TwitchClipsResponse response = twitchClient.get(url, TwitchClipsResponse.class);

            if (response == null || response.getData() == null) {
                return comments;
            }

            // Map each clip to a Comment
            for (TwitchClip clip : response.getData()) {
                Comment comment = mapClipToComment(clip);
                comments.add(comment);
            }

        } catch (RestClientException e) {
            throw new TwitchApiException("Error fetching clips from Twitch API", e);
        }

        return comments;
    }

    /**
     * Convert a TwitchClip to a Comment object.
     * The clip's title and metadata are combined into the comment text.
     * 
     * @param clip The clip to convert
     * @return A Comment representing the clip
     */
    private Comment mapClipToComment(TwitchClip clip) {
        Comment comment = new Comment();

        comment.setId(clip.getId());
        comment.setCreatedOn(clip.getCreatedAt());

        // Combine clip information into comment text
        // Format: "[CLIP] Title by Creator - Views: X - URL: ..."
        String text = String.format(
                "[CLIP] %s by %s (%d views)\n%s",
                clip.getTitle(),
                clip.getCreatorName(),
                clip.getViewCount() != null ? clip.getViewCount() : 0,
                clip.getUrl()
        );

        comment.setText(text);

        return comment;
    }
}
