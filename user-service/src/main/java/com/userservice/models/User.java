package com.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
@Entity
@NoArgsConstructor// to tell to store this in database
public class User {
    private String name;
    @Id
    private String emailId;
    private String password;
}
