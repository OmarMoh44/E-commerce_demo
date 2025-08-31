package org.ecommerce.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ecommerce.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class Scheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    @Caching(
            evict = {
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "user", allEntries = true)
            }
    )
    public void removeDeletedAccounts() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        int removedCount = userRepository.deleteAccounts(cutoffDate);
        log.info("Deleted {} accounts older than 30 days.", removedCount);
    }

}
