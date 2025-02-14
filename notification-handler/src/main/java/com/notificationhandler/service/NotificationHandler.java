package com.notificationhandler.service;

import com.notificationhandler.dto.MessageDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationHandler {
    @KafkaListener(topics = "notificationTopic", groupId ="notificationGroup", containerFactory = "kafkaListenerContainerFactory")
    public void listenGroupFoo(MessageDTO message) {
        System.out.println("Received Message topic:notificationTopic " + message);
    }
}
