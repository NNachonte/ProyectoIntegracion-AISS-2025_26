package aiss_L3.DailyMotionMiner;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import aiss_L3.DailyMotionMiner.model.videominer.Caption;
import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.model.videominer.Comment;
import aiss_L3.DailyMotionMiner.model.videominer.Video;
import aiss_L3.DailyMotionMiner.services.ChannelService;

@SpringBootTest
class ChannelServiceTest {

    @Autowired
    private ChannelService channelService;

    @Test
    void testRealDailyMotionDataExtraction() {
        System.out.println("\n=======================================================");
        System.out.println("🚀 INICIANDO TEST DE INTEGRACIÓN REAL CON DAILYMOTION");
        System.out.println("=======================================================\n");

        // 1. Obtener la lista general de canales
        List<Channel> allChannels = channelService.getChannels();
        
        if (allChannels == null || allChannels.isEmpty()) {
            System.out.println("❌ No se ha podido recuperar ningún canal de Dailymotion.");
            return;
        }

        // 2. Filtrar para coger solo los 10 primeros
        List<Channel> top10Channels = allChannels.stream()
                .limit(10)
                .collect(Collectors.toList());
        
        System.out.println("✅ Se han extraído " + top10Channels.size() + " canales correctamente. Buscando vídeos...\n");

        // 3. Recorrer los canales buscando uno que tenga datos para imprimir
        boolean dataFound = false;

        for (Channel basicChannel : top10Channels) {
            // Hacemos la llamada profunda para sacar el canal con sus vídeos (1 vídeo por página, 1 página)
            Channel fullChannel = channelService.getChannelById(basicChannel.getId(), 1, 1);

            if (fullChannel != null && fullChannel.getVideos() != null && !fullChannel.getVideos().isEmpty()) {

                System.out.println("=======================================================");
                System.out.println("✅ Canal con vídeos encontrado: " + fullChannel.toString());
                
                System.out.println("-------------------------------------------------------");
                System.out.println("📺 DATOS DEL CANAL (Usuario Dailymotion)");
                System.out.println("-------------------------------------------------------");
                System.out.println("ID:          " + fullChannel.getId());
                System.out.println("Nombre:      " + fullChannel.getName());
                System.out.println("Descripción: " + fullChannel.getDescription());
                System.out.println("Fecha Creación: " + fullChannel.getCreatedTime());

                Video video = fullChannel.getVideos().get(0);
                System.out.println("\n-------------------------------------------------------");
                System.out.println("🎬 DATOS DEL VÍDEO");
                System.out.println("-------------------------------------------------------");
                System.out.println("ID:          " + video.getId());
                System.out.println("Título:      " + video.getName());
                System.out.println("Descripción: " + video.getDescription());
                System.out.println("Fecha Salida:" + video.getReleaseTime());

                System.out.println("\n-------------------------------------------------------");
                System.out.println("📝 DATOS DEL SUBTÍTULO (Caption)");
                System.out.println("-------------------------------------------------------");
                if (video.getCaptions() != null && !video.getCaptions().isEmpty()) {
                    Caption caption = video.getCaptions().get(0);
                    System.out.println("ID:          " + caption.getId());
                    System.out.println("Idioma:      " + caption.getLanguage());
                } else {
                    System.out.println("⚠️ Este vídeo no tiene subtítulos.");
                }

                System.out.println("\n-------------------------------------------------------");
                System.out.println("💬 DATOS DEL COMENTARIO (Generado desde los Tags)");
                System.out.println("-------------------------------------------------------");
                if (video.getComments() != null && !video.getComments().isEmpty()) {
                    Comment comment = video.getComments().get(0);
                    System.out.println("ID:          " + comment.getId());
                    System.out.println("Texto (Tag): " + comment.getText());
                    System.out.println("Fecha:       " + comment.getCreatedOn());
                } else {
                    System.out.println("⚠️ Este vídeo no tiene tags/comentarios.");
                }
                
                System.out.println("=======================================================\n");

                dataFound = true;
                break; // Rompemos el bucle porque ya hemos impreso un ejemplo completo
            }
        }

        if (!dataFound) {
            System.out.println("⚠️ Se revisaron 10 canales, pero ninguno tenía vídeos públicos accesibles.");
        }
    }
}