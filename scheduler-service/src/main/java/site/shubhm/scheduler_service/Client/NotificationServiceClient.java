package site.shubhm.scheduler_service.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.shubhm.notification_service.dto.NotificationRequest;
import site.shubhm.notification_service.dto.NotificationResponse;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
	@PostMapping("/api/notifications/send")
	NotificationResponse sendNotification(@RequestBody NotificationRequest request);
}