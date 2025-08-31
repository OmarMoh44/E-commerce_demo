package org.ecommerce.backend.repository;

import jakarta.transaction.Transactional;
import org.ecommerce.backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.id = ?1")
    void deleteById(Long id);
}
