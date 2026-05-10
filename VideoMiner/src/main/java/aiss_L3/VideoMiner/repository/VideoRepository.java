// VideoRepository.java
package aiss_L3.VideoMiner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import aiss_L3.VideoMiner.model.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String>,JpaSpecificationExecutor<Video> {}
