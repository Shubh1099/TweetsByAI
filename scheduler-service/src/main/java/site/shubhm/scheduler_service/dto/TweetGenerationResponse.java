package site.shubhm.scheduler_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetGenerationResponse {
	private Long id;
	private String tweetContent;
	private Long newsArticleId;
}
