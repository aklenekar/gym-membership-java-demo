package com.apexgym.shared.initializers;

import com.apexgym.shared.initializers.seeders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!prod")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class MasterDataSeeder {

    private final UserSeeder userSeeder;
    private final TrainerSeeder trainerSeeder;
    private final ClassSeeder classSeeder;
    private final DashboardDemoSeeder dashboardDemoSeeder;
    private final ComprehensiveSeeder comprehensiveSeeder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        log.info("🚀 Starting database seeding workflow...");

        userSeeder.seed();
        trainerSeeder.seed();
        classSeeder.seed();
        dashboardDemoSeeder.seed();
        comprehensiveSeeder.seed();

        log.info("✅ Database seeding completed successfully!");
    }
}
