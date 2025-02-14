package com.notificationscheduler.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private String groupId ;
    private String recurrence;
    private String message;
    private String triggerTime;

}
