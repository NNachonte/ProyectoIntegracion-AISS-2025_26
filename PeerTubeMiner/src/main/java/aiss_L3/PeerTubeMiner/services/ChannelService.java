package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
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
    String url = "https://peertube2.cpy.re/api/v1/video-channels";
    ChannelSearchPt channelsJson = restTemplate.getForObject(url, ChannelSearchPt.class);

    List<Channel> channelsTransformados = new ArrayList<>();

    if (channelsJson != null && channelsJson.getData() != null) {
        // Limitamos a los 10 primeros para que el test no sea eterno
        for (ChannelPT channelJson : channelsJson.getData()) {
            if (channelsTransformados.size() >= 10) break; 

            try {
                Channel channel = transformer.transformChannel(channelJson);

                // Intentamos obtener los vídeos del canal
                String videosUrl = url + "/" + channelJson.getName() + "/videos";
                VideoSearchPT videosJson = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

                if (videosJson != null && videosJson.getData() != null) {
                    for (VideoPT vpt : videosJson.getData()) {
                        Video videoCompleto = videoService.getVideoById(vpt.getUuid(), 2);
                        channel.getVideos().add(videoCompleto);
                    }
                }
                channelsTransformados.add(channel);

            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                // Si el canal da 404, imprimimos un aviso y saltamos al siguiente
                System.out.println("Saltando canal no encontrado: " + channelJson.getName());
            } catch (RestClientException e) {
                // Captura cualquier otro error (timeout, etc.) para que no rompa el test
                System.out.println("Error procesando canal " + channelJson.getName() + ": " + e.getMessage());
            }
        }
    }
    return channelsTransformados;
}

    public Channel getChannelById(String id) {
        try{
            // 1. Obtenemos el canal (Usando peertube2 como ya vimos)
            String baseUrl = "https://peertube2.cpy.re/api/v1";
            ChannelPT ptChannel = restTemplate.getForObject(baseUrl + "/video-channels/" + id, ChannelPT.class);
            Channel channel = transformer.transformChannel(ptChannel);

            // 2. En lugar de ir al endpoint del canal, vamos al general de vídeos
            String videosUrl = baseUrl + "/videos"; 
            VideoSearchPT allVideos = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

            if (allVideos != null && allVideos.getData() != null) {
                for (VideoPT vpt : allVideos.getData()) {
                    // 3. Comprobamos si el vídeo pertenece a este canal
                    // PeerTube suele incluir el objeto channel dentro de cada vídeo
                    if (vpt.getChannel() != null && vpt.getChannel().getId().equals(ptChannel.getId())) {
                
                        // 4. Si coincide, lo procesamos con tu videoService (que ya tiene el filtro de comentarios)
                        Video videoTransformado = videoService.getVideoById(vpt.getUuid(), 2);
                
                        if (videoTransformado != null) {
                            channel.getVideos().add(videoTransformado);
                    }
                    }
                }
            }
            return channel;
        }catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.err.println("ERROR: El canal con ID '" + id + "' no existe en PeerTube.");
            return null;
        } catch (RestClientException e) {
            System.err.println("ERROR inesperado: " + e.getMessage());
            return null;
        }
    }

    public Channel postChannel(String id, Integer maxVideos, Integer maxComments) {
    try {
        // 1. Definimos los límites
        int vLimit = (maxVideos != null) ? maxVideos : 10;
        int cLimit = (maxComments != null) ? maxComments : 2;

        // 2. Intentamos recuperar el canal de PeerTube
        // Aquí es donde saltaba el error 404 si el ID no era correcto
        String baseUrl = "https://peertube2.cpy.re/api/v1/video-channels/" + id;
        ChannelPT ptChannel = restTemplate.getForObject(baseUrl, ChannelPT.class);
        
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

        // 4. Enviamos el objeto final a VideoMiner
        String videoMinerUrl = "http://localhost:8080/videominer/channels";
        System.out.println("Enviando canal a VideoMiner: " + channel.getName());
        return restTemplate.postForObject(videoMinerUrl, channel, Channel.class);

    } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
        // Capturamos específicamente el error 404
        System.err.println("ERROR: El canal con ID '" + id + "' no existe en PeerTube.");
        return null; // Devolvemos null para que el controlador sepa que no hubo éxito
    } catch (RestClientException e) {
        // Capturamos cualquier otro error (conexión, validación, etc.)
        System.err.println("ERROR inesperado: " + e.getMessage());
        return null;
    }
}
    
}
