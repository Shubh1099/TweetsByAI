package site.shubhm.notification_service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.shubhm.notification_service.Entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findBySentAtAfter(LocalDateTime dateTime);
}