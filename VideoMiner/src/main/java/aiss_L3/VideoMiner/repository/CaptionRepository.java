// CaptionRepository.java
package aiss_L3.VideoMiner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import aiss_L3.VideoMiner.model.Caption;

@Repository
public interface CaptionRepository extends JpaRepository<Caption, String>,JpaSpecificationExecutor<Caption> {}