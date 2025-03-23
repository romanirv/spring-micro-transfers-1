package ru.otus.java.pro.mt.core.transfers.services;

import ru.otus.java.pro.mt.core.transfers.dtos.ExecuteTransferDtoRq;
import ru.otus.java.pro.mt.core.transfers.entities.Transfer;
import ru.otus.java.pro.mt.core.transfers.kafka.messages.TransferStatus;

import java.util.List;
import java.util.Optional;

public interface TransfersService {
    Optional<Transfer> getTransferById(String id, String clientId);
    List<Transfer> getAllTransfers(String clientId);
    void execute(String clientId, ExecuteTransferDtoRq executeTransferDtoRq);
    void save(Transfer transfer);
    void sendNotification(TransferStatus transferStatus);
}
