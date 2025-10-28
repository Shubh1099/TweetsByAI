package site.shubhm.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetGenerationRequest {
	private Long newsArticleId;
	private String title;
	private String description;
}