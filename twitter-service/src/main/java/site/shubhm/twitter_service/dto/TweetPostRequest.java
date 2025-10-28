package site.shubhm.twitter_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetPostRequest {
	private Long generatedTweetId;
	private String tweetContent;
}
