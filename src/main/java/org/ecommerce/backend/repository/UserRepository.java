package org.ecommerce.backend.repository;

import org.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.isDeleted = true AND u.deletedDate <= :cutoffDate")
    int deleteAccounts(@Param("cutoffDate") LocalDate cutoffDate);
}
