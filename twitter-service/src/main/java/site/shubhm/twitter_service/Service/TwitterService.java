package site.shubhm.twitter_service.Service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import site.shubhm.twitter_service.Entity.PostedTweet;
import site.shubhm.twitter_service.Repository.PostedTweetRepository;
import site.shubhm.twitter_service.dto.TweetPostRequest;
import site.shubhm.twitter_service.dto.TweetPostResponse;
import site.shubhm.twitter_service.dto.TwitterApiRequest;
import site.shubhm.twitter_service.dto.TwitterApiResponse;

import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwitterService {
	private final PostedTweetRepository repository;
	private final WebClient.Builder webClientBuilder;

	@Value("${twitter.api.key}")
	private String consumerKey;

	@Value("${twitter.api.key-secret}")
	private String consumerSecret;

	@Value("${twitter.api.access-token}")
	private String accessToken;

	@Value("${twitter.api.access-token-secret}")
	private String accessTokenSecret;

	@Value("${twitter.api.url}")
	private String apiUrl;

	@Value("${twitter.api.mock:false}")
	private boolean mockMode;

	public TweetPostResponse postTweet(TweetPostRequest request) {
		log.info("Posting tweet: {}", request.getTweetContent());

		if (repository.existsByGeneratedTweetId(request.getGeneratedTweetId())) {
			log.warn("Tweet already posted for generatedTweetId: {}",
					request.getGeneratedTweetId());
			return null;
		}


		if (mockMode) {
			return mockPostTweet(request);
		}

		try {
			// OAuth 1.0a Authentication
			String url = apiUrl + "/tweets";
			String method = "POST";

			// Generate OAuth signature
			Map<String, String> oauthParams = generateOAuthParams();
			String signature = generateOAuthSignature(method, url, oauthParams, request.getTweetContent());
			oauthParams.put("oauth_signature", signature);

			// Build Authorization header
			String authHeader = buildAuthorizationHeader(oauthParams);

			log.debug("OAuth Authorization Header: {}", authHeader);

			TwitterApiRequest twitterRequest = new TwitterApiRequest(
					request.getTweetContent()
			);

			TwitterApiResponse response = webClientBuilder.build()
																				.post()
																				.uri(url)
																				.header("Authorization", authHeader)
																				.header("Content-Type", "application/json")
																				.bodyValue(twitterRequest)
																				.retrieve()
																				.onStatus(
																						status -> status.is4xxClientError() || status.is5xxServerError(),
																						clientResponse -> clientResponse.bodyToMono(String.class)
																																	.map(body -> {
																																		log.error("Twitter API Error: {} - {}", clientResponse.statusCode(), body);
																																		return new RuntimeException("Twitter API Error: " + body);
																																	})
																				)
																				.bodyToMono(TwitterApiResponse.class)
																				.block();

			if (response != null && response.getData() != null) {
				PostedTweet postedTweet = new PostedTweet();
				postedTweet.setGeneratedTweetId(request.getGeneratedTweetId());
				postedTweet.setTweetId(response.getData().getId());
				postedTweet.setTweetContent(request.getTweetContent());
				postedTweet.setPostedAt(LocalDateTime.now());
				postedTweet.setStatus("SUCCESS");

				postedTweet = repository.save(postedTweet);
				log.info("Tweet posted successfully with ID: {}",
						response.getData().getId());

				return new TweetPostResponse(
						postedTweet.getId(),
						postedTweet.getTweetId(),
						postedTweet.getStatus(),
						postedTweet.getPostedAt()
				);
			}
		} catch (WebClientResponseException e) {
			log.error("Error posting tweet: Status {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

			// Save failed attempt
			PostedTweet failedTweet = new PostedTweet();
			failedTweet.setGeneratedTweetId(request.getGeneratedTweetId());
			failedTweet.setTweetContent(request.getTweetContent());
			failedTweet.setPostedAt(LocalDateTime.now());
			failedTweet.setStatus("FAILED: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			repository.save(failedTweet);
		} catch (Exception e) {
			log.error("Error posting tweet: ", e);

			// Save failed attempt
			PostedTweet failedTweet = new PostedTweet();
			failedTweet.setGeneratedTweetId(request.getGeneratedTweetId());
			failedTweet.setTweetContent(request.getTweetContent());
			failedTweet.setPostedAt(LocalDateTime.now());
			failedTweet.setStatus("FAILED: " + e.getMessage());
			repository.save(failedTweet);
		}

		return null;
	}

	// Mock mode for testing without Twitter API
	private TweetPostResponse mockPostTweet(TweetPostRequest request) {
		log.info("MOCK MODE: Simulating tweet post");

		PostedTweet postedTweet = new PostedTweet();
		postedTweet.setGeneratedTweetId(request.getGeneratedTweetId());
		postedTweet.setTweetId("MOCK-" + UUID.randomUUID().toString());
		postedTweet.setTweetContent(request.getTweetContent());
		postedTweet.setPostedAt(LocalDateTime.now());
		postedTweet.setStatus("SUCCESS_MOCK");

		postedTweet = repository.save(postedTweet);
		log.info("MOCK: Tweet 'posted' with ID: {}", postedTweet.getTweetId());

		return new TweetPostResponse(
				postedTweet.getId(),
				postedTweet.getTweetId(),
				postedTweet.getStatus(),
				postedTweet.getPostedAt()
		);
	}

	// Generate OAuth 1.0a parameters
	private Map<String, String> generateOAuthParams() {
		Map<String, String> params = new TreeMap<>();
		params.put("oauth_consumer_key", consumerKey);
		params.put("oauth_token", accessToken);
		params.put("oauth_signature_method", "HMAC-SHA1");
		params.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		params.put("oauth_nonce", UUID.randomUUID().toString().replaceAll("-", ""));
		params.put("oauth_version", "1.0");
		return params;
	}

	// Generate OAuth 1.0a signature
	private String generateOAuthSignature(String method, String url,
																				Map<String, String> oauthParams, String tweetText) throws Exception {

		// Create signature base string
		Map<String, String> allParams = new TreeMap<>(oauthParams);

		StringBuilder paramString = new StringBuilder();
		for (Map.Entry<String, String> entry : allParams.entrySet()) {
			if (paramString.length() > 0) {
				paramString.append("&");
			}
			paramString.append(encode(entry.getKey()))
					.append("=")
					.append(encode(entry.getValue()));
		}

		String signatureBaseString = method.toUpperCase() + "&" +
																		 encode(url) + "&" +
																		 encode(paramString.toString());

		log.debug("Signature Base String: {}", signatureBaseString);

		// Create signing key
		String signingKey = encode(consumerSecret) + "&" + encode(accessTokenSecret);

		// Generate signature
		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec secretKey = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
		mac.init(secretKey);
		byte[] signatureBytes = mac.doFinal(signatureBaseString.getBytes(StandardCharsets.UTF_8));

		return Base64.getEncoder().encodeToString(signatureBytes);
	}

	// Build Authorization header
	private String buildAuthorizationHeader(Map<String, String> oauthParams) {
		StringBuilder header = new StringBuilder("OAuth ");
		boolean first = true;

		for (Map.Entry<String, String> entry : oauthParams.entrySet()) {
			if (!first) {
				header.append(", ");
			}
			header.append(encode(entry.getKey()))
					.append("=\"")
					.append(encode(entry.getValue()))
					.append("\"");
			first = false;
		}

		return header.toString();
	}

	// URL encode
	private String encode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
								 .replace("+", "%20")
								 .replace("*", "%2A")
								 .replace("%7E", "~");
		} catch (Exception e) {
			throw new RuntimeException("Encoding error", e);
		}
	}
}