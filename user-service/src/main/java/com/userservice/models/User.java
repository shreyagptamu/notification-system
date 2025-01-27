package com.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
@Entity // to tell to store this in database
public class User {
    private String name;
    @Id
    private String emailId;
    private String password;
}
