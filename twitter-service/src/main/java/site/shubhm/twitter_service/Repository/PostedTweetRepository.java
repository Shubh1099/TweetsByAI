package site.shubhm.twitter_service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.shubhm.twitter_service.Entity.PostedTweet;

@Repository
public interface PostedTweetRepository extends JpaRepository<PostedTweet, Long> {
	boolean existsByGeneratedTweetId(Long generatedTweetId);
}
