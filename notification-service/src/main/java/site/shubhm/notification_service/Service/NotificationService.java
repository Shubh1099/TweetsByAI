package site.shubhm.notification_service.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import site.shubhm.notification_service.Entity.Notification;
import site.shubhm.notification_service.Repository.NotificationRepository;
import site.shubhm.notification_service.dto.NotificationRequest;
import site.shubhm.notification_service.dto.NotificationResponse;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository repository;
	private final JavaMailSender mailSender;

	@Value("${notification.email.from}")
	private String fromEmail;

	@Value("${notification.email.enabled:true}")
	private boolean emailEnabled;

	public NotificationResponse sendNotification(NotificationRequest request) {
		log.info("Sending {} notification to: {}",
				request.getType(), request.getRecipient());

		Notification notification = new Notification();
		notification.setType(request.getType());
		notification.setRecipient(request.getRecipient());
		notification.setSubject(request.getSubject());
		notification.setMessage(request.getMessage());
		notification.setMetadata(request.getMetadata());
		notification.setSentAt(LocalDateTime.now());

		try {
			switch (request.getType().toUpperCase()) {
				case "EMAIL":
					sendEmail(request);
					notification.setStatus("SENT");
					break;
				case "WEBHOOK":
					sendWebhook(request);
					notification.setStatus("SENT");
					break;
				case "CONSOLE":
					logToConsole(request);
					notification.setStatus("LOGGED");
					break;
				default:
					notification.setStatus("UNSUPPORTED_TYPE");
			}
		} catch (Exception e) {
			log.error("Failed to send notification: ", e);
			notification.setStatus("FAILED: " + e.getMessage());
		}

		notification = repository.save(notification);

		return new NotificationResponse(
				notification.getId(),
				notification.getStatus(),
				notification.getSentAt()
		);
	}

	private void sendEmail(NotificationRequest request) {
		if (!emailEnabled) {
			log.warn("Email notifications are disabled");
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(request.getRecipient());
		message.setSubject(request.getSubject());
		message.setText(request.getMessage());

		mailSender.send(message);
		log.info("Email sent successfully to: {}", request.getRecipient());
	}

	private void sendWebhook(NotificationRequest request) {
		// Implement webhook logic here if needed
		log.info("Webhook notification: {}", request.getMessage());
	}

	private void logToConsole(NotificationRequest request) {
		log.info("=".repeat(50));
		log.info("NOTIFICATION: {}", request.getSubject());
		log.info("Message: {}", request.getMessage());
		log.info("Metadata: {}", request.getMetadata());
		log.info("=".repeat(50));
	}
}