package site.shubhm.scheduler_service.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.shubhm.twitter_service.dto.TweetPostRequest;
import site.shubhm.twitter_service.dto.TweetPostResponse;

@FeignClient(name = "twitter-service")
public interface TwitterServiceClient {
	@PostMapping("/api/twitter/post-tweet")
	TweetPostResponse postTweet(@RequestBody TweetPostRequest request);
}