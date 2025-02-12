package com.contactservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Data
@Table(name = "groups")
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    private String groupId;
    private String userId;
    private String groupName;

}
