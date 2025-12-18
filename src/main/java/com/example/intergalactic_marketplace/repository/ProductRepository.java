package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.entity.ProductEntity;
import com.example.intergalactic_marketplace.repository.projection.ProductBasicProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends NaturalIdRepository<ProductEntity, String>, JpaRepository<ProductEntity, String> {
    boolean existsByName(String name);

    Page<ProductBasicProjection> findAllBy(Pageable pageable);

    @Query("SELECT p FROM ProductEntity AS p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keword, '%'))")
    Page<ProductBasicProjection> searchByName(String keyword, Pageable pageable);
}
