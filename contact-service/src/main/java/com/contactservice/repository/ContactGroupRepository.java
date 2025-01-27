package com.contactservice.repository;

import com.contactservice.models.ContactGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactGroupRepository extends CrudRepository<ContactGroup,String> {
}
