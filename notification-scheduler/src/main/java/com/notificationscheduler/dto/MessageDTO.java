package com.notificationscheduler.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDTO {
    private String message;
    private String emailId;
    private String phoneNumber;

}
