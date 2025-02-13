package com.notificationscheduler.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private String emailId;
    private String phoneNumber;
}
