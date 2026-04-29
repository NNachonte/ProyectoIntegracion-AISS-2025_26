package aiss_L3.DailyMotionMiner.etl;

import java.util.ArrayList;
import java.time.Instant;

import org.springframework.stereotype.Component;

import aiss_L3.DailyMotionMiner.model.dailymotion.CaptionDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.ChannelDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.CommentDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.UserDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.VideoDM;
import aiss_L3.DailyMotionMiner.model.videominer.Caption;
import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.model.videominer.Comment;
import aiss_L3.DailyMotionMiner.model.videominer.User;
import aiss_L3.DailyMotionMiner.model.videominer.Video;

@Component
public class Transformer {
    
    public Video transformVideo(VideoDM dmVideo) {
        if (dmVideo == null) return null;

        Video video = new Video();
        video.setId(dmVideo.getId());
        video.setName(dmVideo.getTitle());
        video.setDescription(dmVideo.getDescription());
        video.setReleaseTime(epochSecondsToIso(dmVideo.getCreatedTime()));

        video.setAuthor(transformUser(dmVideo));

        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());

        return video;
    }

    public Channel transformChannel(ChannelDM dmChannel) {
        if (dmChannel == null) return null;

        Channel channel = new Channel();
        channel.setId(dmChannel.getId());
        channel.setName(dmChannel.getName());
        channel.setDescription(dmChannel.getDescription());
        channel.setCreatedTime(epochSecondsToIso(dmChannel.getCreatedTime()));
        channel.setVideos(new ArrayList<>());
        return channel;
    }

    public Channel transformChannel(VideoDM dmVideo) {
        if (dmVideo == null) return null;
        if (dmVideo.getChannelId() == null && dmVideo.getChannelName() == null && dmVideo.getChannelDescription() == null
                && dmVideo.getChannelCreatedTime() == null) {
            return null;
        }

        Channel channel = new Channel();
        channel.setId(dmVideo.getChannelId());
        channel.setName(dmVideo.getChannelName());
        channel.setDescription(dmVideo.getChannelDescription());
        channel.setCreatedTime(epochSecondsToIso(dmVideo.getChannelCreatedTime()));
        channel.setVideos(new ArrayList<>());
        return channel;
    }

    public User transformUser(UserDM dmUser) {
        if (dmUser == null) return null;

        User user = new User();
        user.setId(parseLongOrNull(dmUser.getId()));
        user.setName(dmUser.getScreenname());
        user.setUser_link(dmUser.getUrl());
        user.setPicture_link(dmUser.getAvatar720Url());
        return user;
    }

    public User transformUser(VideoDM dmVideo) {
        if (dmVideo == null) return null;
        if (dmVideo.getOwnerScreenname() == null && dmVideo.getOwnerUrl() == null && dmVideo.getOwnerAvatar720Url() == null
                && dmVideo.getOwnerId() == null) {
            return null;
        }

        User user = new User();
        user.setId(parseLongOrNull(dmVideo.getOwnerId()));
        user.setName(dmVideo.getOwnerScreenname());
        user.setUser_link(dmVideo.getOwnerUrl());
        user.setPicture_link(dmVideo.getOwnerAvatar720Url());
        return user;
    }

    public Caption transformCaption(CaptionDM dmCaption) {
        if (dmCaption == null || dmCaption.getLanguage() == null) return null;

        Caption caption = new Caption();
        caption.setId(dmCaption.getId());
        caption.setName(dmCaption.getLanguageLabel() != null ? dmCaption.getLanguageLabel() : dmCaption.getLanguage());
        caption.setLanguage(dmCaption.getLanguage());
        return caption;
    }

    public Comment transformComment(CommentDM dmComment) {
        if (dmComment == null) return null;

        Comment comment = new Comment();
        comment.setId(dmComment.getId());
        comment.setText(dmComment.getMessage());
        comment.setCreatedOn(epochSecondsToIso(dmComment.getCreatedTime()));
        return comment;
    }

    private static String epochSecondsToIso(Integer epochSeconds) {
        if (epochSeconds == null) return null;
        return Instant.ofEpochSecond(epochSeconds.longValue()).toString();
    }

    private static Long parseLongOrNull(String value) {
        if (value == null) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
}
