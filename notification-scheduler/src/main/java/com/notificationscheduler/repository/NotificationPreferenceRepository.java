package com.notificationscheduler.repository;

import com.notificationscheduler.models.NotificationPreference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationPreferenceRepository extends CrudRepository<NotificationPreference,String> {

    public List<NotificationPreference> findByUserId(String userId);
}
