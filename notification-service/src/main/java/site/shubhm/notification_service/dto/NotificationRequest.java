package site.shubhm.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
	private String type;
	private String recipient;
	private String subject;
	private String message;
	private String metadata;
}
