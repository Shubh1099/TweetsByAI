package site.shubhm.scheduler_service.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.shubhm.scheduler_service.Service.OrchestrationService;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
	private final OrchestrationService orchestrationService;

	@PostMapping("/trigger")
	public ResponseEntity<String> triggerWorkflow() {
		orchestrationService.triggerManualWorkflow();
		return ResponseEntity.ok("Workflow triggered successfully");
	}
}
