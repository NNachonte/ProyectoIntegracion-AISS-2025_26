package aiss_L3.TwitchMiner.etl;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import aiss_L3.TwitchMiner.model.twitch.TwitchUser;
import aiss_L3.TwitchMiner.model.twitch.TwitchVideo;
import aiss_L3.TwitchMiner.model.videominer.Channel;
import aiss_L3.TwitchMiner.model.videominer.User;
import aiss_L3.TwitchMiner.model.videominer.Video;

@Component
public class Transformer {

    public Channel transformChannel(TwitchUser twitchUser) {
        if (twitchUser == null) return null;

        Channel channel = new Channel();
        channel.setId(twitchUser.getId());
        channel.setName(firstNonEmpty(twitchUser.getDisplayName(), twitchUser.getLogin(), twitchUser.getId()));
        channel.setDescription(twitchUser.getDescription());
        channel.setCreatedTime(twitchUser.getCreatedAt());
        channel.setVideos(new ArrayList<>());
        return channel;
    }

    public Video transformVideo(TwitchVideo twitchVideo, TwitchUser twitchUser) {
        if (twitchVideo == null) return null;

        Video video = new Video();
        video.setId(twitchVideo.getId());
        video.setName(twitchVideo.getTitle());
        video.setDescription(twitchVideo.getDescription());
        video.setReleaseTime(twitchVideo.getCreatedAt());

        if (twitchUser != null) {
            video.setAuthor(transformUser(twitchUser));
        } else {
            video.setAuthor(transformUser(twitchVideo));
        }

        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        return video;
    }

    public User transformUser(TwitchUser twitchUser) {
        if (twitchUser == null) return null;

        User user = new User();
        user.setId(null);
        user.setName(firstNonEmpty(twitchUser.getDisplayName(), twitchUser.getLogin(), twitchUser.getId()));
        if (twitchUser.getLogin() != null && !twitchUser.getLogin().isBlank()) {
            user.setUser_link("https://www.twitch.tv/" + twitchUser.getLogin());
        }
        user.setPicture_link(twitchUser.getProfileImageUrl());
        return user;
    }

    public User transformUser(TwitchVideo twitchVideo) {
        if (twitchVideo == null) return null;

        User user = new User();
        user.setId(null);
        user.setName(firstNonEmpty(twitchVideo.getUserName(), twitchVideo.getUserLogin(), twitchVideo.getUserId()));
        if (twitchVideo.getUserLogin() != null && !twitchVideo.getUserLogin().isBlank()) {
            user.setUser_link("https://www.twitch.tv/" + twitchVideo.getUserLogin());
        }
        return user;
    }

    private static String firstNonEmpty(String first, String second, String third) {
        if (first != null && !first.isBlank()) return first;
        if (second != null && !second.isBlank()) return second;
        return third;
    }
}
