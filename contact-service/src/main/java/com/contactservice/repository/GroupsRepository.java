package com.contactservice.repository;

import com.contactservice.models.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupsRepository extends CrudRepository<Group,String> {
}
