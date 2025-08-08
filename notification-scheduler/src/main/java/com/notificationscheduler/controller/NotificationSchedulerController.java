package com.notificationscheduler.controller;

import com.notificationscheduler.dto.NotificationPreferenceDTO;
import com.notificationscheduler.dto.NotificationReadDTO;
import com.notificationscheduler.models.NotificationPreference;
import com.notificationscheduler.service.NotificationSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationSchedulerController {

    @Autowired
    private NotificationSchedulerService notificationSchedulerService;

    public NotificationSchedulerController(NotificationSchedulerService notificationSchedulerService) {
        this.notificationSchedulerService = notificationSchedulerService;
    }

    @PostMapping("/api/notification/preference")
    public ResponseEntity<String> createNotificationSchedule(@RequestBody NotificationPreferenceDTO notificationPreferenceDTO) throws URISyntaxException {
        String id=notificationSchedulerService.createNotificationSchedule(notificationPreferenceDTO);
        String location="/api/notification/preference"+id;
        return ResponseEntity.created(new URI(location)).build();
    }

    @GetMapping("/api/notification/preference")
    public List<NotificationPreference> getNotificationPreferences(@RequestBody NotificationReadDTO notificationReadDTO){
        return notificationSchedulerService.getNotificationPreferences(notificationReadDTO.getUserId());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
    }
}
