package aiss_L3.PeerTubeMiner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import aiss_L3.PeerTubeMiner.model.videominer.Comment;
import aiss_L3.PeerTubeMiner.model.videominer.Video;
import aiss_L3.PeerTubeMiner.services.VideoService;

@SpringBootTest
class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @Test
    @DisplayName("Obtener un vídeo detallado con límite de comentarios")
    public void getVideoById() {
        // Tomamos un UUID "vivo" desde el listado para evitar IDs hardcoded que
        // puedan dejar de existir o causar 404.
        List<Video> videos = videoService.getVideos();
        assertNotNull(videos, "La lista de vídeos no debería ser nula");
        assertFalse(videos.isEmpty(), "Debería devolver al menos un vídeo para poder probar getVideoById");

        String videoUuid = videos.get(0).getId();
        Integer maxComments = 3;

        // 2. Ejecutar la llamada
        Video video = videoService.getVideoById(videoUuid, maxComments);

        // 3. Verificaciones de integridad
        assertNotNull(video, "El vídeo no debería ser nulo");
        System.out.println("Validando vídeo: " + video.getName());

        // Comprobar campos de VideoMiner
        assertNotNull(video.getId(), "El ID (UUID) debe estar presente");
        assertNotNull(video.getReleaseTime(), "La fecha de publicación debe estar mapeada");

        // 4. Verificación de Comentarios (La parte crítica)
        assertNotNull(video.getComments(), "La lista de comentarios no debe ser nula");
        assertTrue(video.getComments().size() <= maxComments, 
            "El número de comentarios (" + video.getComments().size() + ") no debe superar el máximo pedido (" + maxComments + ")");
        
        if (!video.getComments().isEmpty()) {
            Comment firstComment = video.getComments().get(0);
            assertNotNull(firstComment.getText(), "El texto del comentario debe existir");
            assertNotNull(firstComment.getCreatedOn(), "La fecha de creación del comentario debe existir");
            System.out.println("Ejemplo de comentario obtenido: " + firstComment.toString());
        }

        // 5. Verificación de Captions (Subtítulos)
        assertNotNull(video.getCaptions(), "La lista de subtítulos debe existir");
        System.out.println("  -> Subtítulos encontrados: " + video.getCaptions().size());
    }

    @Test
    @DisplayName("Listar vídeos globales")
    void getVideos() {
        List<Video> videos = videoService.getVideos();
        
        assertNotNull(videos);
        assertFalse(videos.isEmpty(), "Debería devolver una lista de vídeos de la plataforma");
        
        // Verificamos que el primer vídeo de la lista global también se haya procesado
        assertNotNull(videos.get(0).getName());
    }
}