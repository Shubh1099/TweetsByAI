package site.shubhm.ai_service.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import site.shubhm.ai_service.Entity.GeneratedTweet;
import site.shubhm.ai_service.Repository.GeneratedTweetRepository;
import site.shubhm.ai_service.dto.GeminiRequest;
import site.shubhm.ai_service.dto.GeminiResponse;
import site.shubhm.ai_service.dto.TweetGenerationRequest;
import site.shubhm.ai_service.dto.TweetGenerationResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
	private final GeneratedTweetRepository repository;
	private final WebClient.Builder webClientBuilder;

	@Value("${gemini.api.key}")
	private String apiKey;

	@Value("${gemini.api.url}")
	private String apiUrl;

	public TweetGenerationResponse generateSarcasticTweet(TweetGenerationRequest request) {
		log.info("Generating sarcastic tweet for news: {}", request.getTitle());

		if (repository.existsByNewsArticleId(request.getNewsArticleId())) {
			log.warn("Tweet already generated for article: {}", request.getNewsArticleId());
			return null;
		}

		String prompt = buildPrompt(request.getTitle(), request.getDescription());
		String tweetContent = callGeminiApi(prompt);

		if (tweetContent != null) {
			GeneratedTweet tweet = new GeneratedTweet();
			tweet.setNewsArticleId(request.getNewsArticleId());
			tweet.setOriginalTitle(request.getTitle());
			tweet.setTweetContent(tweetContent);
			tweet.setGeneratedAt(LocalDateTime.now());

			tweet = repository.save(tweet);
			log.info("Generated and saved tweet: {}", tweet.getTweetContent());

			return new TweetGenerationResponse(
					tweet.getId(),
					tweet.getTweetContent(),
					tweet.getNewsArticleId()
			);
		}

		return null;
	}

	private String buildPrompt(String title, String description) {
		return String.format(
				"Create a witty, sarcastic tweet (max 280 characters) about this news headline: '%s'. " +
						"Description: %s. " +
						"Make it clever and funny, but not offensive. Use subtle sarcasm and irony. " +
						"Don't use hashtags. Just return the tweet text, nothing else.",
				title, description != null ? description : ""
		);
	}

	private String callGeminiApi(String prompt) {
		try {
			GeminiRequest.Part part = new GeminiRequest.Part(prompt);
			GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
			GeminiRequest request = new GeminiRequest(List.of(content));

			GeminiResponse response = webClientBuilder.build()
																		.post()
																		.uri(apiUrl + "?key=" + apiKey)
																		.header("Content-Type", "application/json")
																		.bodyValue(request)
																		.retrieve()
																		.bodyToMono(GeminiResponse.class)
																		.block();

			if (response != null &&
							response.getCandidates() != null &&
							!response.getCandidates().isEmpty()) {
				String generatedText = response.getCandidates().get(0)
																	 .getContent()
																	 .getParts()
																	 .get(0)
																	 .getText();

				// Ensure tweet is within 280 characters
				return generatedText.length() > 280
									 ? generatedText.substring(0, 277) + "..."
									 : generatedText;
			}
		} catch (Exception e) {
			log.error("Error calling Gemini API: ", e);
		}

		return null;
	}

	public List<TweetGenerationResponse> getUnpostedTweets() {
		return repository.findByPostedFalseOrderByGeneratedAtDesc()
							 .stream()
							 .map(tweet -> new TweetGenerationResponse(
									 tweet.getId(),
									 tweet.getTweetContent(),
									 tweet.getNewsArticleId()
							 ))
							 .toList();
	}

	public void markAsPosted(Long id) {
		repository.findById(id).ifPresent(tweet -> {
			tweet.setPosted(true);
			tweet.setPostedAt(LocalDateTime.now());
			repository.save(tweet);
			log.info("Marked tweet {} as posted", id);
		});
	}
}
