package com.kafkaproducer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Sender {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;


    public void sendMessage(String message){
        kafkaTemplate.send("kafkaLearning", String.valueOf(1),message);
    }
}
