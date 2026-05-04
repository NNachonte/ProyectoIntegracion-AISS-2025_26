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

        System.out.println("=================================================================");
        System.out.println("Validando canal: " + firstChannel.toString());
        System.out.println("=================================================================");

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
    }

    @Test
    @DisplayName("Debe devolver un canal válido dado un ID existente")
    void testGetChannelById() {
        // Tomamos un canal "vivo" desde el listado para evitar IDs hardcoded
        // que puedan dejar de existir o causar 404.
        List<Channel> channels = channelService.getChannels();
        assertNotNull(channels, "La lista de canales no debería ser nula");
        assertFalse(channels.isEmpty(), "Debería devolver al menos un canal para poder probar getChannelById");

        // Nota: el listado puede incluir canales remotos que no resuelven en
        // /video-channels/{name}. Probamos hasta encontrar uno válido.
        Channel channel = null;
        String channelId = null;

        for (Channel candidate : channels) {
            if (candidate == null || candidate.getId() == null) continue;
            channelId = candidate.getId();
            channel = channelService.getChannelById(channelId);
            if (channel != null) break;
        }

        // 3. Assert (Comprobar que el resultado es el esperado)
        assertNotNull(channel, "No se encontró ningún canal del listado que resolviera correctamente por ID (" + channelId + ")");
        assertNotNull(channel.getId(), "El ID del canal mapeado no debería ser null");
        assertNotNull(channel.getName(), "El canal debe tener un nombre");

        // Si el canal 15 tiene vídeos, podemos comprobar que la lista se inicializa bien
        assertNotNull(channel.getVideos(), "La lista de vídeos no debe ser null (aunque puede estar vacía)");
        
        // Comprobamos que el ID y/o el nombre tienen datos (opcionalmente puedes comprobar el nombre exacto si lo sabes)
        System.out.println("Canal recuperado en el test: " + channel.getName());
        System.out.println("Canal recuperado: " + channel.toString());
        
    }
}
