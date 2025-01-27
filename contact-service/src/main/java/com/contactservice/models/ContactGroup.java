package com.contactservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Data
public class ContactGroup {
@Id
private String contactGroupId;
private String groupId;
private String contactId;

}
