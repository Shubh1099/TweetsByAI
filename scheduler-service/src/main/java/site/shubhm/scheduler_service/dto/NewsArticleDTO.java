package site.shubhm.scheduler_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleDTO {
	private Long id;
	private String title;
	private String description;
	private String url;
	private String source;
	private LocalDateTime publishedAt;
	private boolean processed;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetGenerationRequest {
	private Long newsArticleId;
	private String title;
	private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetGenerationResponse {
	private Long id;
	private String tweetContent;
	private Long newsArticleId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetPostRequest {
	private Long generatedTweetId;
	private String tweetContent;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetPostResponse {
	private Long id;
	private String tweetId;
	private String status;
	private LocalDateTime postedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class NotificationRequest {
	private String type;
	private String recipient;
	private String subject;
	private String message;
	private String metadata;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class NotificationResponse {
	private Long id;
	private String status;
	private LocalDateTime sentAt;
}