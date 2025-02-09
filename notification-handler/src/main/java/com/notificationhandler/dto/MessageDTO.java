package com.notificationhandler.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MessageDTO {
    private String message;
    private String emailId;
    private String phoneNumber;
}
