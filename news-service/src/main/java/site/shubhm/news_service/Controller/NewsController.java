package site.shubhm.news_service.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shubhm.news_service.Service.NewsService;
import site.shubhm.news_service.dto.NewsArticleDTO;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
	private final NewsService newsService;

	@PostMapping("/fetch")
	public ResponseEntity<List<NewsArticleDTO>> fetchNews() {
		return ResponseEntity.ok(newsService.fetchLatestNews());
	}

	@GetMapping("/unprocessed")
	public ResponseEntity<List<NewsArticleDTO>> getUnprocessedNews() {
		return ResponseEntity.ok(newsService.getUnprocessedNews());
	}

	@PutMapping("/{id}/mark-processed")
	public ResponseEntity<Void> markAsProcessed(@PathVariable Long id) {
		newsService.markAsProcessed(id);
		return ResponseEntity.ok().build();
	}
}