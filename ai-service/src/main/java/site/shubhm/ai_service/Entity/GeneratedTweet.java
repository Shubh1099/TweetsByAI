package site.shubhm.ai_service.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "generated_tweets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedTweet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long newsArticleId;

	@Column(length = 2000)
	private String originalTitle;

	@Column(length = 500)
	private String tweetContent;

	private LocalDateTime generatedAt;
	private boolean posted = false;
	private LocalDateTime postedAt;
}