package site.shubhm.scheduler_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
class TweetPostResponse {
	private Long id;
	private String tweetId;
	private String status;
	private LocalDateTime postedAt;
}
