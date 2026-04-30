// VideoRepository.java
package aiss_L3.VideoMiner.repository;
import aiss_L3.VideoMiner.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {}
