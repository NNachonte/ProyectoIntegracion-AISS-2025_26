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

        // En PeerTube, los endpoints /video-channels/{id} suelen esperar el "name" (handle),
        // no el ID numérico interno. Usamos name como identificador estable.
        channel.setId(ptChannel.getName() != null ? ptChannel.getName() : (ptChannel.getId() != null ? String.valueOf(ptChannel.getId()) : null));
        channel.setName(ptChannel.getDisplayName());
        channel.setDescription(ptChannel.getDescription());
        channel.setCreatedTime(ptChannel.getCreatedAt());

        channel.setVideos(new ArrayList<>());

        return channel;
    }

    public User transformUser(AccountPT ptAccount) {
        if (ptAccount == null) return null;

        User user = new User();

        // IMPORTANTE (persistencia en VideoMiner):
        // VideoMiner usa IDs autogenerados para User. Si enviamos un id fijo (p.ej. 3)
        // para muchos vídeos, JPA intentará insertar duplicados y el POST fallará.
        // Dejarlo a null permite que VideoMiner genere IDs únicos.
        user.setId(null);
        user.setName(ptAccount.getName());
        user.setUser_link(ptAccount.getUrl());
        user.setPicture_link(ptAccount.getAvatars() != null 
        && !ptAccount.getAvatars().isEmpty() ? ptAccount.getAvatars().get(0).getFileUrl() : null);

        return user;
    }

    public Caption transformCaption(CaptionPT ptCaption, String videoId) {
        if (ptCaption == null || ptCaption.getLanguage() == null) return null;

        Caption caption = new Caption();

        // IMPORTANTE (persistencia en VideoMiner):
        // En el modelo, Caption.id es la PK. Si usamos solo el language id ("en"),
        // colisiona entre vídeos. Hacemos el id único por vídeo.
        String langId = ptCaption.getLanguage().getId();
        String safeVideoId = videoId != null ? videoId : "video";
        caption.setId(safeVideoId + "_" + langId);
        caption.setName(ptCaption.getLanguage().getLabel());
        caption.setLanguage(langId);

        return caption;
    }

    public Comment transformComment(CommentPT ptComment, String videoId) {
        if (ptComment.getText() == null || ptComment.getText().trim().isEmpty()) {
            return null;
        }

        Comment comment = new Comment();

        // Hacemos el id único por vídeo para evitar colisiones entre vídeos/canales.
        String safeVideoId = videoId != null ? videoId : "video";
        comment.setId(safeVideoId + "_" + ptComment.getId());
        comment.setText(ptComment.getText());
        comment.setCreatedOn(ptComment.getCreatedAt());
        

        return comment;
    }
    
}
