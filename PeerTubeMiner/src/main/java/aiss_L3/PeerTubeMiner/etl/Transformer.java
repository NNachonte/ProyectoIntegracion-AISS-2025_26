package aiss_L3.PeerTubeMiner.etl;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import aiss_L3.PeerTubeMiner.model.peertube.AccountPT;
import aiss_L3.PeerTubeMiner.model.peertube.CaptionPT;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelPT;
import aiss_L3.PeerTubeMiner.model.peertube.CommentPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.videominer.Caption;
import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.model.videominer.Comment;
import aiss_L3.PeerTubeMiner.model.videominer.User;
import aiss_L3.PeerTubeMiner.model.videominer.Video;

@Component
public class Transformer {
    
    public Video transformVideo(VideoPT ptVideo) {
        if (ptVideo == null) return null;

        Video video = new Video();
        video.setId(ptVideo.getUuid());
        video.setName(ptVideo.getName());
        video.setDescription(ptVideo.getDescription());
        video.setReleaseTime(ptVideo.getPublishedAt());
        
        video.setAuthor(transformUser(ptVideo.getAccount()));
        
        
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        
        return video;
    }

    public Channel transformChannel(ChannelPT ptChannel) {
        if (ptChannel == null) return null;

        Channel channel = new Channel();

        channel.setId(ptChannel.getId() != null ? String.valueOf(ptChannel.getId()) : null);
        channel.setName(ptChannel.getDisplayName());
        channel.setDescription(ptChannel.getDescription());
        channel.setCreatedTime(ptChannel.getCreatedAt());

        channel.setVideos(new ArrayList<>());

        return channel;
    }

    public User transformUser(AccountPT ptAccount) {
        if (ptAccount == null) return null;

        User user = new User();

        user.setId(Long.valueOf(ptAccount.getId()));
        user.setName(ptAccount.getName());
        user.setUser_link(ptAccount.getUrl());
        user.setPicture_link(ptAccount.getAvatars() != null 
        && !ptAccount.getAvatars().isEmpty() ? ptAccount.getAvatars().get(0).getFileUrl() : null);

        return user;
    }

    public Caption transformCaption(CaptionPT ptCaption) {
        if (ptCaption == null || ptCaption.getLanguage() == null) return null;

        Caption caption = new Caption();

        caption.setId(ptCaption.getLanguage().getId());
        caption.setName(ptCaption.getLanguage().getLabel());
        caption.setLanguage(ptCaption.getLanguage().getId());

        return caption;
    }

    public Comment transformComment(CommentPT ptComment) {
        if (ptComment == null) return null;

        Comment comment = new Comment();

        comment.setId(String.valueOf(ptComment.getId()));
        comment.setText(ptComment.getText());
        comment.setCreatedOn(ptComment.getCreatedAt());
        comment.setAuthor(transformUser(ptComment.getAccount()));
        

        return comment;
    }
    
}
