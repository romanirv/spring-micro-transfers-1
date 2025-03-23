package ru.otus.java.pro.mt.core.transfers.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.mt.core.transfers.kafka.messages.TransferStatus;

@Component
public class KafkaProducerSender {

    KafkaTemplate<String, Object> kafkaTemplate;

    @Value(value = "${app.sending.topic.name}")
    String kafkaTopic;

    public KafkaProducerSender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(TransferStatus message) {
        kafkaTemplate.send(kafkaTopic, message);
    }

}
