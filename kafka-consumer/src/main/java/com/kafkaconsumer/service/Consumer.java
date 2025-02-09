package com.kafkaconsumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    @KafkaListener(topics = "kafkaLearning", groupId ="kl")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message topic:kafkaLearning " + message);
    }
}
