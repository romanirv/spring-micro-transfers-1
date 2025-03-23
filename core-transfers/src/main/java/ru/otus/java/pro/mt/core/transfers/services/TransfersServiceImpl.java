package ru.otus.java.pro.mt.core.transfers.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.java.pro.mt.core.transfers.configs.properties.TransfersProperties;
import ru.otus.java.pro.mt.core.transfers.dtos.ExecuteTransferDtoRq;
import ru.otus.java.pro.mt.core.transfers.entities.Transfer;
import ru.otus.java.pro.mt.core.transfers.exceptions_handling.BusinessLogicException;
import ru.otus.java.pro.mt.core.transfers.kafka.KafkaProducerSender;
import ru.otus.java.pro.mt.core.transfers.kafka.messages.TransferStatus;
import ru.otus.java.pro.mt.core.transfers.repositories.TransfersRepository;
import ru.otus.java.pro.mt.core.transfers.validators.TransferRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransfersServiceImpl implements TransfersService {
    private final Logger logger = LoggerFactory.getLogger(TransfersServiceImpl.class);

    private final TransfersRepository transfersRepository;
    private final TransferRequestValidator transferRequestValidator;
    private final TransfersProperties transfersProperties;
    private final LimitsServiceImpl limitsService;
    private final KafkaProducerSender kafkaSender;

    @Override
    public Optional<Transfer> getTransferById(String id, String clientId) {
        return transfersRepository.findByIdAndClientId(id, clientId);
    }

    @Override
    public List<Transfer> getAllTransfers(String clientId) {
        return transfersRepository.findAllByClientId(clientId);
    }

    @Override
    public void execute(String clientId, ExecuteTransferDtoRq executeTransferDtoRq) {
        transferRequestValidator.validate(executeTransferDtoRq);

        Transfer transfer = new Transfer(UUID.randomUUID().toString(), clientId,
                executeTransferDtoRq.getTargetClientId(),
                executeTransferDtoRq.getSourceAccount(),
                executeTransferDtoRq.getTargetAccount(),
                executeTransferDtoRq.getMessage(),
                executeTransferDtoRq.getAmount());

        // execution
        if (!limitsService.isLimitEnough()) {
            sendNotification(new TransferStatus(transfer.getId(), "LIMIT_ERROR"));
            throw new BusinessLogicException("Transfer limit error", "LIMIT_ERROR");
        }

        if (executeTransferDtoRq.getAmount().compareTo(transfersProperties.getMaxTransferSum()) > 0) {
            sendNotification(new TransferStatus(transfer.getId(), "MAX_TRANSFER_SUM_ERROR"));
            throw new BusinessLogicException("Max transfer amount has been exceeded", "MAX_TRANSFER_SUM_ERROR");
        }
        save(transfer);
        sendNotification(new TransferStatus(transfer.getId(), "EXECUTED"));
    }

    @Override
    public void save(Transfer transfer) {
        logger.info("Save transfer {}", transfer);
        transfersRepository.save(transfer);
    }

    @Override
    public void sendNotification(TransferStatus transferStatus) {
        logger.info("Send notification {}", transferStatus);
        kafkaSender.send(transferStatus);
    }
}
