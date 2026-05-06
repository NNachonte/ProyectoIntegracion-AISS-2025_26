package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
import aiss_L3.PeerTubeMiner.exception.PeerTubeApiException;
import aiss_L3.PeerTubeMiner.exception.ResourceNotFoundException;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelPT;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelSearchPt;
import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoSearchPT;
import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.model.videominer.Video;

@Service
public class ChannelService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    VideoService videoService;

    public List<Channel> getChannels() {
        // Listado ligero: solo devuelve metadatos del canal.
        // Evitamos enriquecer con vídeos/detalles para no disparar rate-limits (429).
        String url = "https://peertube.tv/api/v1/video-channels?count=10";
        ChannelSearchPt channelsJson;
        try {
            channelsJson = restTemplate.getForObject(url, ChannelSearchPt.class);
        } catch (RestClientException e) {
            throw new PeerTubeApiException("Error fetching channels from PeerTube API", e);
        }

        if (channelsJson == null) {
            throw new PeerTubeApiException("Unexpected error communicating with PeerTube");
        }

        List<Channel> channelsTransformados = new ArrayList<>();

        if (channelsJson != null && channelsJson.getData() != null) {
            for (ChannelPT channelJson : channelsJson.getData()) {
                if (channelsTransformados.size() >= 10) break;

                try {
                    Channel channel = transformer.transformChannel(channelJson);
                    channelsTransformados.add(channel);
                } catch (RestClientException e) {
                    System.out.println("Error procesando canal " + channelJson.getName() + ": " + e.getMessage());
                }
            }
        }

        return channelsTransformados;
    }

    public Channel getChannelById(String id) {
        try {
            String baseUrl = "https://peertube.tv/api/v1";
            ChannelPT ptChannel = restTemplate.getForObject(baseUrl + "/video-channels/" + id, ChannelPT.class);
            if (ptChannel == null) {
                throw new PeerTubeApiException("PeerTube returned an empty response for channel '" + id + "'.");
            }
            Channel channel = transformer.transformChannel(ptChannel);

            // Recuperamos vídeos del canal desde el endpoint específico del canal
            // (mucho menos costoso que escanear /videos global y filtrar).
            String videosUrl = baseUrl + "/video-channels/" + id + "/videos?count=10";
            VideoSearchPT videosJson = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

            if (videosJson != null && videosJson.getData() != null) {
                for (VideoPT vpt : videosJson.getData()) {
                    // Importante: el endpoint /video-channels/{id}/videos ya devuelve objetos vídeo
                    // completos (con uuid, name, publishedAt, account, etc.). Si para cada vídeo
                    // hacemos otra llamada a /videos/{uuid} podemos caer en rate-limit (429) y
                    // terminar devolviendo un canal sin vídeos.
                    Video videoTransformado = transformer.transformVideo(vpt);
                    if (videoTransformado != null) channel.getVideos().add(videoTransformado);
                }
            }
            return channel;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Channel not found on PeerTube: " + id);
        } catch (RestClientException e) {
            throw new PeerTubeApiException("Unexpected error communicating with PeerTube", e);
        }
    }

    public Channel postChannel(String id, Integer maxVideos, Integer maxComments) {
        try {
            // 1. Definimos los límites
            int vLimit = (maxVideos != null) ? maxVideos : 10;
            int cLimit = (maxComments != null) ? maxComments : 2;

            // 2. Intentamos recuperar el canal de PeerTube
            // Aquí es donde saltaba el error 404 si el ID no era correcto
            String baseUrl = "https://peertube.tv/api/v1/video-channels/" + id;
            ChannelPT ptChannel = restTemplate.getForObject(baseUrl, ChannelPT.class);
            if (ptChannel == null) {
                throw new PeerTubeApiException("Unexpected error communicating with PeerTube");
            }

            // Transformamos el canal
            Channel channel = transformer.transformChannel(ptChannel);

            // 3. Intentamos recuperar los vídeos del canal
            String videosUrl = baseUrl + "/videos?count=" + vLimit;
            VideoSearchPT videosJson = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

            if (videosJson != null && videosJson.getData() != null) {
                for (VideoPT vpt : videosJson.getData()) {
                    // Obtenemos cada vídeo con sus comentarios
                    Video videoCompleto = videoService.getVideoById(vpt.getUuid(), cLimit);

                    // IMPORTANTE: Asegúrate de que videoService.getVideoById
                    // ya filtre los comentarios vacíos "" para no romper VideoMiner
                    if (videoCompleto != null) {
                        channel.getVideos().add(videoCompleto);
                    }
                }
            }

            return sendChannelToVideoMiner(channel);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Channel not found on PeerTube: " + id);
        } catch (RestClientException e) {
            throw new PeerTubeApiException("Unexpected error communicating with PeerTube", e);
        }
    }

    private Channel sendChannelToVideoMiner(Channel channel) {
        try {
            // 4. Enviamos el objeto final a VideoMiner
            // Nota: VideoMiner puede responder 201 sin body. En ese caso, devolvemos
            // el canal que hemos construido (para que este POST no sea "vacío").
            String videoMinerUrl = "http://localhost:8080/videominer/channels";
            System.out.println("Enviando canal a VideoMiner: " + channel.getName());

            Channel createdInVideoMiner = restTemplate.postForObject(videoMinerUrl, channel, Channel.class);
            return createdInVideoMiner != null ? createdInVideoMiner : channel;
        } catch (RestClientException e) {
            throw new PeerTubeApiException("Unexpected error communicating with VideoMiner", e);
        }
    }

}
