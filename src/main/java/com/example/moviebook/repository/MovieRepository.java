package com.example.moviebook.repository;

import com.example.moviebook.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    boolean existsByTitle(String title);

    List<MovieEntity> findByGrade(String grade);

    @Query("SELECT DISTINCT m.grade FROM MovieEntity m WHERE m.grade IS NOT NULL")
    List<String> findDistinctGrades();

    @Query("SELECT m.posterPath FROM MovieEntity m WHERE m.title = :title")
    String findPosterPathByTitle(@Param("title") String title);

    Optional<MovieEntity> findByTitle(String title);
}
