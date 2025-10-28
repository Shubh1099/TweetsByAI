package site.shubhm.notification_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type; // EMAIL, SMS, WEBHOOK
	private String recipient;

	@Column(length = 1000)
	private String message;

	private String subject;
	private LocalDateTime sentAt;
	private String status;

	@Column(length = 2000)
	private String metadata;
}