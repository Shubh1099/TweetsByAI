package site.shubhm.news_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "site.shubhm.news_service")
public class NewsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(NewsServiceApplication.class, args);
	}
}
