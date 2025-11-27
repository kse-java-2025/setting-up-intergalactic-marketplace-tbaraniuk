package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.entity.ProductEntity;
import com.example.intergalactic_marketplace.repository.projection.ProductBasicProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsByName(String name);

    Optional<ProductEntity> findByUuid(UUID productId);

    Page<ProductBasicProjection> findAllBy(Pageable pageable);

    @Query("SELECT p FROM ProductEntity AS p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keword, '%'))")
    Page<ProductBasicProjection> searchByName(String keyword, Pageable pageable);

    boolean existsByUuid(UUID productId);

    void deleteByUuid(UUID productId);
}
