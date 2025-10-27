package site.shubhm.news_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleDTO {
	private Long id;
	private String title;
	private String description;
	private String url;
	private String source;
	private LocalDateTime publishedAt;
	private boolean processed;
}
