package edu.java.scheduler;

import edu.java.service.updater.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final UpdateService linkUpdater;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("Running scheduled update...");
        try {
            linkUpdater.update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
