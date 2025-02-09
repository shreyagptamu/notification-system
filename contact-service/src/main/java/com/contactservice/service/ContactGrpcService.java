package com.contactservice.service;

import com.grpcdefinitions.ContactOuterClass;
import com.grpcdefinitions.ContactServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ContactGrpcService extends ContactServiceGrpc.ContactServiceImplBase {

    @Override
    public void getContactsFromContactGroup(ContactOuterClass.ContactGroupRequest request, StreamObserver<ContactOuterClass.ContactsResponse> responseObserver) {

    }
}
