package site.shubhm.ai_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shubhm.ai_service.Service.GeminiService;
import site.shubhm.ai_service.dto.TweetGenerationRequest;
import site.shubhm.ai_service.dto.TweetGenerationResponse;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {
	private final GeminiService geminiService;

	@PostMapping("/generate-tweet")
	public ResponseEntity<TweetGenerationResponse> generateTweet(
			@RequestBody TweetGenerationRequest request) {
		TweetGenerationResponse response = geminiService.generateSarcasticTweet(request);
		return response != null
							 ? ResponseEntity.ok(response)
							 : ResponseEntity.noContent().build();
	}

	@GetMapping("/unposted-tweets")
	public ResponseEntity<List<TweetGenerationResponse>> getUnpostedTweets() {
		return ResponseEntity.ok(geminiService.getUnpostedTweets());
	}

	@PutMapping("/tweets/{id}/mark-posted")
	public ResponseEntity<Void> markAsPosted(@PathVariable Long id) {
		geminiService.markAsPosted(id);
		return ResponseEntity.ok().build();
	}
}