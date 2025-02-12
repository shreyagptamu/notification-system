package com.userservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Data

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    String token;
    @ManyToOne // Correct annotation for a single User
    @JoinColumn(name = "emailId", nullable = false)
    User user;
    Timestamp timestamp;
}
