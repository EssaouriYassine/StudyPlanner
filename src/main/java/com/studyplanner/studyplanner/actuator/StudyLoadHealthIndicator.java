package com.studyplanner.studyplanner.actuator;

import com.studyplanner.studyplanner.repository.StudySessionRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StudyLoadHealthIndicator implements HealthIndicator {

    private final StudySessionRepository repository;

    public StudyLoadHealthIndicator(StudySessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        long totalSessions = repository.count();

        if (totalSessions > 50) {
            return Health.down()
                    .withDetail("totalSessions", totalSessions)
                    .withDetail("status", "Surcharge critique")
                    .build();
        }

        if (totalSessions > 30) {
            return Health.up()
                    .withDetail("totalSessions", totalSessions)
                    .withDetail("status", "Charge élevée - warning")
                    .build();
        }

        return Health.up()
                .withDetail("totalSessions", totalSessions)
                .withDetail("status", "Charge normale")
                .build();
    }
}
