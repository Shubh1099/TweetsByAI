package site.shubhm.twitter_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "posted_tweets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostedTweet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long generatedTweetId;
	private String tweetId;

	@Column(length = 500)
	private String tweetContent;

	private LocalDateTime postedAt;
	private String status;
}