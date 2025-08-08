package com.contactservice.service;

import com.contactservice.dto.GroupDTO;
import com.grpcdefinitions.ContactOuterClass;
import com.grpcdefinitions.ContactServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@GrpcService
public class ContactGrpcService extends ContactServiceGrpc.ContactServiceImplBase {
    @Autowired
    private ContactService contactService;

    @Override
    public void getContactsFromContactGroup(ContactOuterClass.ContactGroupRequest request, StreamObserver<ContactOuterClass.ContactsResponse> responseObserver) {
        Optional<GroupDTO> groupInfo= contactService.readGroup(request.getContactGroupId());
        List<ContactOuterClass.Contact> contacts = new ArrayList<>();
        groupInfo.get().getContacts().forEach(contact -> {
            ContactOuterClass.Contact contactResponse= ContactOuterClass.Contact.newBuilder().setEmailId(contact.getEmailId()).setPhoneNumber(contact.getPhoneNo()).build();
            contacts.add(contactResponse);
        });
        ContactOuterClass.ContactsResponse contactsResponse= ContactOuterClass.ContactsResponse.newBuilder().addAllContacts(contacts).build();
        responseObserver.onNext(contactsResponse);
        responseObserver.onCompleted();
    }

    void test2(){
        System.out.println("sdsdd");
    }
}
