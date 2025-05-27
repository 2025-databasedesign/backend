package com.example.moviebook.repository;

import com.example.moviebook.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    boolean existsByTitle(String title);

    List<MovieEntity> findByRating(String rating);

    @Query("SELECT DISTINCT m.rating FROM MovieEntity m WHERE m.rating IS NOT NULL")
    List<String> findDistinctRatings();
}