package com.example.moviebook.repository;

import com.example.moviebook.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    boolean existsByTitle(String title);

    List<MovieEntity> findByGrade(String grade);

    @Query("SELECT DISTINCT m.grade FROM MovieEntity m WHERE m.grade IS NOT NULL")
    List<String> findDistinctGrades();
}