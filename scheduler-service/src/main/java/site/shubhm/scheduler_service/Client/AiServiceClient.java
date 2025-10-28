package site.shubhm.scheduler_service.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.shubhm.ai_service.dto.TweetGenerationRequest;
import site.shubhm.ai_service.dto.TweetGenerationResponse;

import java.util.List;

@FeignClient(name = "ai-service")
public interface AiServiceClient {
	@PostMapping("/api/ai/generate-tweet")
	TweetGenerationResponse generateTweet(@RequestBody TweetGenerationRequest request);

	@GetMapping("/api/ai/unposted-tweets")
	List<TweetGenerationResponse> getUnpostedTweets();

	@PutMapping("/api/ai/tweets/{id}/mark-posted")
	void markAsPosted(@PathVariable Long id);
}
