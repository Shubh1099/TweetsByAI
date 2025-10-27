package site.shubhm.news_service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {
	private String status;
	private int totalResults;
	private List<Article> results;

	@Data
	public static class Article {
		@JsonProperty("article_id")
		private String articleId;

		private String title;
		private String link;
		private String description;
		private String content;

		@JsonProperty("pubDate")
		private String pubDate;

		@JsonProperty("source_id")
		private String sourceId;

		@JsonProperty("source_name")
		private String sourceName;
	}
}