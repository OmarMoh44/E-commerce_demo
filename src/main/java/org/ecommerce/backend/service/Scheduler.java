package org.ecommerce.backend.service;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class Scheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    public void removeDeletedAccounts() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        int removedCount = userRepository.deleteAccounts(cutoffDate);
        // TODO: log the count of deleted accounts
        System.out.println("Deleted " + removedCount + " accounts older than 30 days.");
    }

}
