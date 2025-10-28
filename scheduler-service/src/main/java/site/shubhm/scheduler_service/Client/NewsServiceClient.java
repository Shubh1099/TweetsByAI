package site.shubhm.scheduler_service.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.shubhm.scheduler_service.dto.NewsArticleDTO;

import java.util.List;

@FeignClient(name = "news-service")
public interface NewsServiceClient {
	@PostMapping("/api/news/fetch")
	List<NewsArticleDTO> fetchNews();

	@GetMapping("/api/news/unprocessed")
	List<NewsArticleDTO> getUnprocessedNews();

	@PutMapping("/api/news/{id}/mark-processed")
	void markAsProcessed(@PathVariable Long id);
}