package com.contactservice.dto;

import com.contactservice.models.Contact;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GroupDTO {
    private String userId;
    private String groupName;
    List<Contact> contacts;
}

