package com.contactservice.repository;

import com.contactservice.models.ContactGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactGroupRepository extends CrudRepository<ContactGroup,String> {
    public List<ContactGroup> findAllByGroupId(String groupId);
}
