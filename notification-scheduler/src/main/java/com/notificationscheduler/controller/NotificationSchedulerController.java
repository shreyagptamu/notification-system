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
import java.util.List;

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
        String location="/api/contact/"+id;
        return ResponseEntity.created(new URI(location)).build();
    }

    @GetMapping("/api/notification/preference")
    public List<NotificationPreference> getNotificationPreferences(@RequestBody NotificationReadDTO notificationReadDTO){
        return notificationSchedulerService.getNotificationPreferences(notificationReadDTO.getUserId());
    }
}
