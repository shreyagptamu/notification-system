package com.notificationscheduler.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDTO {
    private String userId;
    private String notificationType;
    private ScheduleDTO notificationSchedule;

}

