package com.notificationscheduler.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationPreferenceDTO {
    private String userId;
    private String notificationType;
    private ScheduleDTO notificationSchedule;

}

