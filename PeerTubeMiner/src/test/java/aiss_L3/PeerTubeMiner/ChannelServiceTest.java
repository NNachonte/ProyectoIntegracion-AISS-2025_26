package aiss_L3.PeerTubeMiner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import aiss_L3.PeerTubeMiner.model.videominer.Caption;
import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.model.videominer.Comment;
import aiss_L3.PeerTubeMiner.model.videominer.Video;
import aiss_L3.PeerTubeMiner.services.ChannelService;

@SpringBootTest
public class ChannelServiceTest {

    @Autowired
    ChannelService channelService;

    @Test
    @DisplayName("Listar canales de PeerTube y comprobar transformación a VideoMiner")
    public void getChannels() {
        // 1. Ejecutar la llamada al servicio
        List<Channel> channels = channelService.getChannels();

        // 2. Verificaciones básicas de la lista
        assertNotNull(channels, "La lista de canales no debería ser nula");
        assertFalse(channels.isEmpty(), "La lista debería contener al menos un canal");

        // 3. Verificación del Modelo de Datos (VideoMiner)
        // Comprobamos el primer canal de la lista
        Channel firstChannel = channels.get(0);
        
        System.out.println("Validando canal: " + firstChannel.getName());

        // Comprobar campos obligatorios del modelo VideoMiner
        assertNotNull(firstChannel.getId(), "El ID del canal de VideoMiner no debe ser nulo");
        assertNotNull(firstChannel.getName(), "El nombre del canal no debe ser nulo");
        assertNotNull(firstChannel.getVideos(), "La lista de vídeos del canal debe existir (aunque esté vacía)");

        // 4. Verificación de la jerarquía (Vídeos dentro del canal)
        if (!firstChannel.getVideos().isEmpty()) {
            Video firstVideo = firstChannel.getVideos().get(0);
            assertNotNull(firstVideo.getId(), "El vídeo debe tener un ID");
            assertNotNull(firstVideo.getName(), "El vídeo debe tener un título");
            
            System.out.println("  -> Vídeo encontrado: " + firstVideo.toString());
            System.out.println("  -> Comentarios encontrados: " + firstVideo.getComments().size());

            List<Comment> comments = firstVideo.getComments();
            assertNotNull(comments, "La lista de comentarios del vídeo debe existir (aunque esté vacía)");
            System.out.println("Ejemplo de comentario: " + (comments.isEmpty() ? "No hay comentarios" : comments.get(0).toString()));

            List<Caption> captions = firstVideo.getCaptions();
            assertNotNull(captions, "La lista de subtítulos del vídeo debe existir (aunque esté vacía)");
            System.out.println("Ejemplo de subtítulo: " + (captions.isEmpty() ? "No hay subtítulos" : captions.get(0).toString()));


        }


        System.out.println("Ejemplo de canal obtenido: " + firstChannel.toString());
        System.out.println("Total de canales obtenidos: " + channels.size());
    }
}
