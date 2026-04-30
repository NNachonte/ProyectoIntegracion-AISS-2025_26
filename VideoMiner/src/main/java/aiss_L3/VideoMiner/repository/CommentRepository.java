// CommentRepository.java
package aiss_L3.VideoMiner.repository;
import aiss_L3.VideoMiner.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {}