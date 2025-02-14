package com.notificationscheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true) // Allows modifications to existing objects
@NoArgsConstructor // Required for Jackson deserialization
@AllArgsConstructor // Ensures all fields have a constructor
@ToString // Generates a readable toString method
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown JSON fields
public class MessageDTO {
    private String message;
    private String emailId;
    private String phoneNumber;
}
