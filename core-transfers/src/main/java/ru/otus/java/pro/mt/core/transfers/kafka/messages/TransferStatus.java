package ru.otus.java.pro.mt.core.transfers.kafka.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferStatus(@JsonProperty String transferId, @JsonProperty String status) {
}
