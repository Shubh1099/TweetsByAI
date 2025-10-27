package site.shubhm.news_service.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import site.shubhm.news_service.Entity.NewsArticle;
import site.shubhm.news_service.Repository.NewsArticleRepository;
import site.shubhm.news_service.dto.NewsApiResponse;
import site.shubhm.news_service.dto.NewsArticleDTO;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
	private final NewsArticleRepository repository;
	private final WebClient.Builder webClientBuilder;

	@Value("${news.api.key}")
	private String apiKey;

	public List<NewsArticleDTO> fetchLatestNews() {
		log.info("Fetching latest news from NewsData.io");

		try {
			String url = String.format(
					"https://newsdata.io/api/1/latest?apikey=%s&country=us&language=en&category=technology",
					apiKey
			);

			NewsApiResponse response = webClientBuilder.build()
																		 .get()
																		 .uri(url)
																		 .retrieve()
																		 .bodyToMono(NewsApiResponse.class)
																		 .block();

			if (response != null && response.getResults() != null) {
				log.info("Fetched {} articles", response.getResults().size());
				return response.getResults().stream()
									 .filter(article -> article.getLink() != null)
									 .filter(article -> !repository.existsByUrl(article.getLink()))
									 .map(this::saveArticle)
									 .collect(Collectors.toList());
			}
		} catch (Exception e) {
			log.error("Error fetching news: ", e);
		}

		return List.of();
	}

	private NewsArticleDTO saveArticle(NewsApiResponse.Article article) {
		NewsArticle entity = new NewsArticle();
		entity.setTitle(article.getTitle());
		entity.setDescription(article.getDescription());
		entity.setUrl(article.getLink());
		entity.setSource(article.getSourceId());
		entity.setContent(article.getContent());
		entity.setFetchedAt(LocalDateTime.now());

		try {
			if (article.getPubDate() != null) {
				entity.setPublishedAt(LocalDateTime.parse(
						article.getPubDate(),
						DateTimeFormatter.ISO_DATE_TIME
				));
			} else {
				entity.setPublishedAt(LocalDateTime.now());
			}
		} catch (Exception e) {
			entity.setPublishedAt(LocalDateTime.now());
		}

		entity = repository.save(entity);
		log.info("Saved news article: {}", entity.getTitle());

		return convertToDTO(entity);
	}

	public List<NewsArticleDTO> getUnprocessedNews() {
		return repository.findByProcessedFalseOrderByPublishedAtDesc()
							 .stream()
							 .map(this::convertToDTO)
							 .collect(Collectors.toList());
	}

	public void markAsProcessed(Long id) {
		repository.findById(id).ifPresent(article -> {
			article.setProcessed(true);
			repository.save(article);
			log.info("Marked article {} as processed", id);
		});
	}

	private NewsArticleDTO convertToDTO(NewsArticle entity) {
		return new NewsArticleDTO(
				entity.getId(),
				entity.getTitle(),
				entity.getDescription(),
				entity.getUrl(),
				entity.getSource(),
				entity.getPublishedAt(),
				entity.isProcessed()
		);
	}
}