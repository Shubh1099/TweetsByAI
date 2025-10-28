package site.shubhm.scheduler_service.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.shubhm.ai_service.dto.TweetGenerationRequest;
import site.shubhm.ai_service.dto.TweetGenerationResponse;
import site.shubhm.notification_service.dto.NotificationRequest;
import site.shubhm.scheduler_service.Client.AiServiceClient;
import site.shubhm.scheduler_service.Client.NewsServiceClient;
import site.shubhm.scheduler_service.Client.NotificationServiceClient;
import site.shubhm.scheduler_service.Client.TwitterServiceClient;
import site.shubhm.scheduler_service.dto.NewsArticleDTO;
import site.shubhm.twitter_service.dto.TweetPostRequest;
import site.shubhm.twitter_service.dto.TweetPostResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestrationService {
	private final NewsServiceClient newsClient;
	private final AiServiceClient aiClient;
	private final TwitterServiceClient twitterClient;
	private final NotificationServiceClient notifierClient;

	@Value("${notification.recipient}")
	private String notificationRecipient;

	@Value("${notification.type:CONSOLE}")
	private String notificationType;

	// Run every 2 hours
	@Scheduled(cron = "0 0 */2 * * *")
	public void executeAutomatedWorkflow() {
		log.info("Starting automated tweet generation workflow");

		try {
			// Step 1: Fetch latest news
			log.info("Step 1: Fetching latest news");
			List<NewsArticleDTO> fetchedNews = newsClient.fetchNews();
			log.info("Fetched {} new articles", fetchedNews.size());

			// Step 2: Get unprocessed news
			List<NewsArticleDTO> unprocessedNews = newsClient.getUnprocessedNews();
			log.info("Found {} unprocessed articles", unprocessedNews.size());

			int successCount = 0;
			int failCount = 0;

			// Step 3: Process each article
			for (NewsArticleDTO article : unprocessedNews) {
				try {
					// Generate sarcastic tweet
					log.info("Step 3: Generating tweet for: {}", article.getTitle());
					TweetGenerationRequest genRequest = new TweetGenerationRequest(
							article.getId(),
							article.getTitle(),
							article.getDescription()
					);

					TweetGenerationResponse genResponse = aiClient.generateTweet(genRequest);

					if (genResponse != null) {
						// Post tweet to Twitter
						log.info("Step 4: Posting tweet to Twitter");
						TweetPostRequest postRequest = new TweetPostRequest(
								genResponse.getId(),
								genResponse.getTweetContent()
						);

						TweetPostResponse postResponse = twitterClient.postTweet(postRequest);

						if (postResponse != null && "SUCCESS".equals(postResponse.getStatus())) {
							// Mark as posted in AI service
							aiClient.markAsPosted(genResponse.getId());

							// Mark as processed in News service
							newsClient.markAsProcessed(article.getId());

							// Send success notification
							sendSuccessNotification(article, genResponse, postResponse);

							successCount++;
							log.info("Successfully posted tweet for article: {}",
									article.getTitle());
						} else {
							failCount++;
							log.warn("Failed to post tweet for article: {}",
									article.getTitle());
						}
					} else {
						failCount++;
						log.warn("Failed to generate tweet for article: {}",
								article.getTitle());
					}

					// Rate limiting - wait between posts
					Thread.sleep(5000);

				} catch (Exception e) {
					failCount++;
					log.error("Error processing article: {}", article.getTitle(), e);
				}
			}

			// Send summary notification
			sendSummaryNotification(successCount, failCount);

			log.info("Workflow completed. Success: {}, Failed: {}",
					successCount, failCount);

		} catch (Exception e) {
			log.error("Error in automated workflow", e);
			sendErrorNotification(e.getMessage());
		}
	}

	private void sendSuccessNotification(NewsArticleDTO article,
																			 TweetGenerationResponse tweet,
																			 TweetPostResponse postResponse) {
		String message = String.format(
				"Tweet posted successfully!\n\n" +
						"Article: %s\n" +
						"Tweet: %s\n" +
						"Twitter ID: %s\n" +
						"Posted at: %s",
				article.getTitle(),
				tweet.getTweetContent(),
				postResponse.getTweetId(),
				postResponse.getPostedAt()
		);

		NotificationRequest notification = new NotificationRequest(
				notificationType,
				notificationRecipient,
				"✅ Tweet Posted Successfully",
				message,
				String.format("{\"articleId\":%d,\"tweetId\":\"%s\"}",
						article.getId(), postResponse.getTweetId())
		);

		try {
			notifierClient.sendNotification(notification);
		} catch (Exception e) {
			log.error("Failed to send success notification", e);
		}
	}

	private void sendSummaryNotification(int successCount, int failCount) {
		String message = String.format(
				"Workflow Summary\n\n" +
						"✅ Successfully posted: %d tweets\n" +
						"❌ Failed: %d tweets\n" +
						"Total processed: %d",
				successCount,
				failCount,
				successCount + failCount
		);

		NotificationRequest notification = new NotificationRequest(
				notificationType,
				notificationRecipient,
				"📊 Tweet Bot Workflow Summary",
				message,
				String.format("{\"success\":%d,\"failed\":%d}",
						successCount, failCount)
		);

		try {
			notifierClient.sendNotification(notification);
		} catch (Exception e) {
			log.error("Failed to send summary notification", e);
		}
	}

	private void sendErrorNotification(String error) {
		NotificationRequest notification = new NotificationRequest(
				notificationType,
				notificationRecipient,
				"❌ Tweet Bot Error",
				"An error occurred in the automated workflow: " + error,
				null
		);

		try {
			notifierClient.sendNotification(notification);
		} catch (Exception e) {
			log.error("Failed to send error notification", e);
		}
	}

	// Manual trigger endpoint
	public void triggerManualWorkflow() {
		log.info("Manual workflow trigger initiated");
		executeAutomatedWorkflow();
	}
}
