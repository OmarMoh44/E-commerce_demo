package org.ecommerce.backend.repository;

import org.ecommerce.backend.dto.ProductMainView;
import org.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select new org.ecommerce.backend.dto.ProductMainView(p.id, p.name, p.description, p.price," +
            "p.stockQuantity, p.active, p.category.name, p.seller.fullName, p.seller.email) from Product p")
    Page<ProductMainView> findAllBy(Pageable pageable);

    @Query("select max(p.price) from Product p")
    Double maxPrice();

    @Query("select new org.ecommerce.backend.dto.ProductMainView(p.id, p.name, p.description, p.price," +
            "p.stockQuantity, p.active, p.category.name, p.seller.fullName, p.seller.email) " +
            "from Product p " +
            "where (:productName is null or LOWER(p.name) like LOWER(concat('%', :productName, '%'))) " +
            "and (:categoryIds is null or p.category.id in :categoryIds) " +
            "and (:minPrice is null or p.price >= :minPrice) " +
            "and (:maxPrice is null or p.price <= :maxPrice)")
    Page<ProductMainView> searchProducts(String productName, List<Long> categoryIds, Double minPrice, Double maxPrice, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.active = false WHERE p.seller.id IN :sellerIdsList")
    void deactivateProducts(List<Long> sellerIdsList);
}
