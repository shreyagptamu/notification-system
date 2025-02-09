package com.notificationscheduler.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleDTO {
    private String groupId ;
    private String recurrence;
    private String message;
    private String triggerTime;

}
