package com.notificationscheduler.service;

import com.grpcdefinitions.ContactOuterClass;
import com.grpcdefinitions.ContactServiceGrpc;
import com.notificationscheduler.dto.ContactDTO;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactGrpcClient {

    @GrpcClient("contactService")
    private ContactServiceGrpc.ContactServiceBlockingStub contactServiceStub;

    public List<ContactDTO> getContacts(String contactGroupId){
        ContactOuterClass.ContactGroupRequest request= ContactOuterClass.ContactGroupRequest.newBuilder().setContactGroupId(contactGroupId).build();
        ContactOuterClass.ContactsResponse response=contactServiceStub.getContactsFromContactGroup(request);
        List<ContactDTO> contacts= new ArrayList<>();
        response.getContactsList().forEach(contact -> {
            ContactDTO contactDTO= ContactDTO.builder().emailId(contact.getEmailId()).phoneNumber(contact.getPhoneNumber()).build();
            contacts.add(contactDTO);
        });
        return contacts;
    }
}
