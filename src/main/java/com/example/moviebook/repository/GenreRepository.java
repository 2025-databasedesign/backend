package com.example.moviebook.repository;

import com.example.moviebook.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

    boolean existsByGenreName(String name);
}
