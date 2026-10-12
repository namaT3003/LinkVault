package com.amy.linkvault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amy.linkvault.model.Link;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    Optional<Link> findByCustomAlias(String customAlias);

    boolean existsByShortCode(String shortCode);

    boolean existsByCustomAlias(String customAlias);

    List<Link> findByCategoryIgnoreCase(String category);

}