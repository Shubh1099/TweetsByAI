package site.shubhm.twitter_service.dto;

import lombok.Data;

@Data
public class TwitterApiResponse {
	private DataObject data;

	@Data
	public static class DataObject {
		private String id;
		private String text;
	}
}