package site.shubhm.ai_service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.shubhm.ai_service.Entity.GeneratedTweet;

import java.util.List;

@Repository
public interface GeneratedTweetRepository extends JpaRepository<GeneratedTweet, Long> {
	List<GeneratedTweet> findByPostedFalseOrderByGeneratedAtDesc();
	boolean existsByNewsArticleId(Long newsArticleId);
}
