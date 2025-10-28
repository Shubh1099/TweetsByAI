package site.shubhm.notification_service.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shubhm.notification_service.Service.NotificationService;
import site.shubhm.notification_service.dto.NotificationRequest;
import site.shubhm.notification_service.dto.NotificationResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	@PostMapping("/send")
	public ResponseEntity<NotificationResponse> sendNotification(
			@RequestBody NotificationRequest request) {
		return ResponseEntity.ok(notificationService.sendNotification(request));
	}
}