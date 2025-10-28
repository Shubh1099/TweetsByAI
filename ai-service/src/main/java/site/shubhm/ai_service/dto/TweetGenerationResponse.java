package site.shubhm.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetGenerationResponse {
	private Long id;
	private String tweetContent;
	private Long newsArticleId;
}