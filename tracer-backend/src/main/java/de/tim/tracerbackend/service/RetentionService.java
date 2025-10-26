package de.tim.tracerbackend.service;

import de.tim.tracerbackend.repository.SpanRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RetentionService {

    private final SpanRepository spanRepository;

    public RetentionService(SpanRepository spanRepository) {
        this.spanRepository = spanRepository;
    }

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void runNightlyRetention() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);

        spanRepository.deleteByTimestampBeforeAndPinnedFalse(cutoff);
    }
}
