package com.notificationscheduler.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {
    @Id
    private String preferenceId;
    private String userId ;
    private String notificationType;
    private String groupId ;
    private String recurrence;
    private String message;
    private String triggerTime;
    private String status;
    private String lastRunOn;
}
