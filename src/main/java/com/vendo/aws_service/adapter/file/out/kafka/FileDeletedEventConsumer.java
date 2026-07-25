package com.vendo.aws_service.adapter.file.out.kafka;

import com.vendo.aws_service.port.file.FileCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeletedEventConsumer {

    private final FileCommandPort fileCommandPort;

    @KafkaListener(
            topics = "${kafka.events.file.deleted-event.topic}",
            groupId = "${kafka.events.file.deleted-event.groupId}",
            properties = {"auto.offset.reset: ${kafka.events.file.deleted-event.properties.auto-offset-reset}"},
            containerFactory = "${kafka.events.file.deleted-event.container-factory}"
    )
    private void listenFileDeletedEvent(String fileKey) {
        log.info("Received event for file deletion: {}", fileKey);
        fileCommandPort.delete(fileKey);
    }

}
