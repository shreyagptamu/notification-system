package com.contactservice.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private String emailId;
    private String phoneNo;
    private String name;

    @Id
    private String contactId;

    private String userId;

}
