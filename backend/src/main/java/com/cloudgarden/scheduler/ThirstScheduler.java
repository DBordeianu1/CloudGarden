package com.cloudgarden.scheduler;

import com.cloudgarden.service.SucculentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThirstScheduler {

    private final SucculentService succulentService;

    @Scheduled(fixedRate = 10000)
    public void updateWaterLevels() {
        log.debug("Starting scheduled water level update for all plants");
        succulentService.updateSucculentWaterLevels();
        log.debug("Scheduled water level update completed");
    }
}
