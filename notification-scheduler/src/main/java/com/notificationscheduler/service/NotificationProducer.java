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

    @Scheduled(cron = "0 */1 * * * *")
    public void produceNotifications(){
        Iterable<NotificationPreference> notificationPreferences= notificationPreferenceRepository.findAll();
        notificationPreferences.forEach(schedule -> {
        if(schedule.getStatus().equals("OPEN")){
            System.out.println("Processing Schedule : " + schedule);
            String triggerTime=schedule.getTriggerTime();
            String recurrence= schedule.getRecurrence();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            // Parse just the time
            LocalTime localTime = LocalTime.parse(triggerTime, formatter);
            // Assume today's date
            LocalDate today = ZonedDateTime.now(ZoneId.of("America/Chicago")).toLocalDate();
            // Combine date and time into a LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(today, localTime);

            ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.of("America/Chicago"));
            System.out.println(localZonedDateTime);
            System.out.println(Instant.now().atZone(ZoneId.of("America/Chicago")));
            if(isScheduleValid(recurrence, schedule.getLastRunOn())){
                System.out.println("Schedule is valid");
            if(localZonedDateTime.isBefore(Instant.now().atZone(ZoneId.of("America/Chicago"))) || localZonedDateTime.equals(Instant.now().atZone(ZoneId.of("America/Chicago")))){
                System.out.println("Reading contacts");
                List<ContactDTO> contactDTOList= contactGrpcClient.getContacts(schedule.getGroupId());
                System.out.println(contactDTOList);
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

    private boolean isScheduleValid(String recurrence, String lastRunOn) {
        if(lastRunOn != null) {
            if (recurrence.equals("DAILY")) {
                if (Duration.between(Instant.parse(lastRunOn), Instant.now()).toHours() >= 24) {
                    return true;
                }
            } else if (recurrence.equals("WEEKLY")) {
                if (Duration.between(Instant.parse(lastRunOn), Instant.now()).toHours() >= 24 * 7) {
                    return true;
                }
            } else {
                if (Duration.between(Instant.parse(lastRunOn), Instant.now()).toHours() >= 24 * 30) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
