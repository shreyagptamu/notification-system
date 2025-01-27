package com.contactservice.repository;

import com.contactservice.models.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends CrudRepository<Contact,String> {
    public Contact findByEmailIdAndPhoneNo(String emailId, String phoneNo);

    public List<Contact> findByUserId(String userId);
}
