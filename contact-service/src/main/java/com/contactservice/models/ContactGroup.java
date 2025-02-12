package com.contactservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroup {
@Id
private String contactGroupId;
private String groupId;
private String contactId;

}
