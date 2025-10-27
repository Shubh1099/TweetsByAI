package site.shubhm.news_service.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 1000)
	private String title;

	@Column(length = 5000)
	private String description;

	@Column(length = 2000)
	private String url;

	private String source;
	private LocalDateTime publishedAt;
	private LocalDateTime fetchedAt;

	@Column(length = 10000)
	private String content;

	private boolean processed = false;


}
