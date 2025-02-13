package com.notificationscheduler.service;

import com.notificationscheduler.dto.ContactDTO;
import com.notificationscheduler.dto.MessageDTO;
import com.notificationscheduler.models.NotificationPreference;
import com.notificationscheduler.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationProducer {

    @Autowired
    private KafkaTemplate<String, MessageDTO> kafkaTemplate;
    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private ContactGrpcClient contactGrpcClient;

    @Scheduled(cron = "0 */5 * * * *")
    public void produceNotifications(){
        Iterable<NotificationPreference> notificationPreferences= notificationPreferenceRepository.findAll();
        notificationPreferences.forEach(schedule -> {
        if(schedule.getStatus().equals("OPEN")){
            String triggerTime=schedule.getTriggerTime();
            String recurrence= schedule.getRecurrence();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            // Parse just the time
            LocalTime localTime = LocalTime.parse(triggerTime, formatter);
            // Assume today's date
            LocalDate today = LocalDate.now();
            // Combine date and time into a LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(today, localTime);
            // Convert to Instant in the system default zone
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            if(isScheduleValid(recurrence, schedule.getLastRunOn(),instant)){
            if(instant.isBefore(Instant.now()) || instant.equals(Instant.now())){
                List<ContactDTO> contactDTOList= contactGrpcClient.getContacts(schedule.getGroupId());
                contactDTOList.forEach((contact) ->{
                    kafkaTemplate.send("notificationTopic", MessageDTO.builder().message(schedule.getMessage()).emailId(contact.getEmailId()).phoneNumber(contact.getPhoneNumber()).build());
                });
                System.out.println("Schedule will be processed");
                if(!schedule.getRecurrence().isEmpty()){
                    schedule.setLastRunOn(Instant.now().toString());
                }else{
                    schedule.setStatus("DONE");
                }
            }
        }}

        });
    }

    private boolean isScheduleValid(String recurrence, String lastRunOn, Instant instant) {
        if(recurrence.equals("DAILY")){
            if(Duration.between(Instant.parse(lastRunOn) ,Instant.now()).toHours()>=24){
                return true;
            }
        }else if(recurrence.equals("WEEKLY")){
            if(Duration.between(Instant.parse(lastRunOn) ,Instant.now()).toHours()>=24*7){
                return true;
            }
        }else{
            if(Duration.between(Instant.parse(lastRunOn) ,Instant.now()).toHours()>=24*30){
                return true;
            }
        }
        return false;
    }
}
