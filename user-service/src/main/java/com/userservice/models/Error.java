package com.userservice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Error {
    private String message;
    private String errorCode;
}
