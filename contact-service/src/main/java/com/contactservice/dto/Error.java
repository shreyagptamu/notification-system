package com.contactservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Error {
    private String message;
    private String errorCode;
}
