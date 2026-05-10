package aiss_L3.VideoMiner;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity; // NUEVO
import org.springframework.http.HttpHeaders; // NUEVO
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss_L3.VideoMiner.model.Caption;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;

public class ChannelTest {

    @Test
    void populateDatabase() {
        // URL de tu API en local
        String url = "http://localhost:8080/videominer/channels";
        RestTemplate restTemplate = new RestTemplate();

        // --- NUEVO: PREPARAMOS LA CABECERA CON TU CLAVE ---
        HttpHeaders headers = new HttpHeaders();
        // OJO: Pon aquí exactamente la misma clave que pusiste en application.properties
        headers.set("X-API-KEY", "clave123"); 

        System.out.println("Comenzando a insertar canales de prueba...");

        // Vamos a insertar 15 canales para que tengas más de una página (si el limit por defecto es 10)
        for (int i = 1; i <= 15; i++) {
            Channel channel = new Channel();
            channel.setId("UC_CHANNEL_" + i); 
            // Alternamos nombres para probar el filtro (q=java o name=java)
            channel.setName(i % 2 == 0 ? "Canal Java " + i : "Canal Spring " + i);
            channel.setDescription("Descripción de prueba para el canal " + i);
            channel.setCreatedTime("2024-01-0" + (i % 9 + 1));

            List<Video> videos = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                Video video = new Video();
                video.setId("VID_" + i + "_" + j);
                video.setName("Tutorial " + j + " del canal " + i);
                video.setDescription("Aprende programación, parte " + j);
                video.setReleaseTime("2024-02-0" + j);

                // Subtítulos
                List<Caption> captions = new ArrayList<>();
                Caption captionEs = new Caption();
                captionEs.setId("CAP_ES_" + i + "_" + j);
                captionEs.setLanguage("es");
                captionEs.setName("Subtítulos en Español");
                captions.add(captionEs);

                Caption captionEn = new Caption();
                captionEn.setId("CAP_EN_" + i + "_" + j);
                captionEn.setLanguage("en");
                captionEn.setName("English Subtitles");
                captions.add(captionEn);

                video.setCaptions(captions);

                // Comentarios
                List<Comment> comments = new ArrayList<>();
                Comment comment = new Comment();
                comment.setId("COM_" + i + "_" + j);
                comment.setText(j % 2 == 0 ? "¡Muy buen video!" : "Tengo una duda con Java...");
                comment.setCreatedOn("2024-03-01");
                comments.add(comment);

                video.setComments(comments);
                
                videos.add(video);
            }
            channel.setVideos(videos);

            try {
                // --- NUEVO: EMPAQUETAMOS EL CANAL Y LA CABECERA JUNTOS ---
                HttpEntity<Channel> requestEntity = new HttpEntity<>(channel, headers);

                // Enviamos el POST usando el requestEntity en lugar del channel a pelo
                ResponseEntity<Channel> response = restTemplate.postForEntity(url, requestEntity, Channel.class);
                System.out.println("✅ Canal " + i + " insertado correctamente. Status: " + response.getStatusCode());
            } catch (RestClientException e) {
                System.err.println("❌ Error al insertar el canal " + i + ": " + e.getMessage());
            }
        }
        
        System.out.println("¡Población de datos finalizada! Ya puedes probar Swagger.");
    }
}