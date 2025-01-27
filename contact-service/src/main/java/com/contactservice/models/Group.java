package com.contactservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Data
@Table(name = "groups")
public class Group {
    @Id
    private String groupId;
    private String userId;
    private String groupName;

}
