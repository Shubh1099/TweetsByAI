package site.shubhm.twitter_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shubhm.twitter_service.Service.TwitterService;
import site.shubhm.twitter_service.dto.TweetPostRequest;
import site.shubhm.twitter_service.dto.TweetPostResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/twitter")
@RequiredArgsConstructor
public class TwitterController {
	private final TwitterService twitterService;

	@PostMapping("/post-tweet")
	public ResponseEntity<TweetPostResponse> postTweet(
			@RequestBody TweetPostRequest request) {
		TweetPostResponse response = twitterService.postTweet(request);
		return response != null
							 ? ResponseEntity.ok(response)
							 : ResponseEntity.noContent().build();
	}
}