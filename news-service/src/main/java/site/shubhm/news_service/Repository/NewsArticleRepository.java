package site.shubhm.news_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.shubhm.news_service.Entity.NewsArticle;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
	List<NewsArticle> findByProcessedFalseOrderByPublishedAtDesc();
	List<NewsArticle> findByFetchedAtAfter(LocalDateTime dateTime);
	boolean existsByUrl(String url);
}