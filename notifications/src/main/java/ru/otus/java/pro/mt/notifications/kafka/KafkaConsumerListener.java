package ru.otus.java.pro.mt.notifications.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.mt.core.transfers.kafka.messages.TransferStatus;

@Component
public class KafkaConsumerListener {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerListener.class);

    @KafkaListener(id = "1", topics = "${app.listening.topic.name}")
    public void listen(TransferStatus message) {
        logger.info("Transfer:{} - status:{}", message.transferId(), message.status());
    }
}