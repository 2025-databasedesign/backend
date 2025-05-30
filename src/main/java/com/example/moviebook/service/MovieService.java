package com.example.moviebook.service;

import com.example.moviebook.dto.MovieDto;
import com.example.moviebook.entity.GenreEntity;
import com.example.moviebook.entity.MovieEntity;
import com.example.moviebook.repository.GenreRepository;
import com.example.moviebook.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    public MovieDto registerMovie(MovieDto dto) {

        if(movieRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("이미 등록된 영화입니다");
        }
        
        MovieEntity entity = new MovieEntity();

        entity.setTitle(dto.getTitle());
        entity.setRunningTime(dto.getRunningTime());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setDirector(dto.getDirector());
        entity.setActors(dto.getActors());
        entity.setGrade(dto.getGrade());
        entity.setFormats(dto.getFormats());

        if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
            List<GenreEntity> genres = genreRepository.findAllById(dto.getGenreIds());
            entity.setGenres(genres);
        }

        MovieEntity saved = movieRepository.save(entity);

        dto.setMovieId(saved.getMovieId());
        return dto;
    }

    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream().map(movie -> {
            List<Long> genreIds = movie.getGenres().stream()
                    .map(GenreEntity::getGenreId)
                    .collect(Collectors.toList());

            List<String> genreNames = movie.getGenres().stream()
                    .map(GenreEntity::getGenreName)
                    .collect(Collectors.toList());

            return new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getRunningTime(),
                    movie.getReleaseDate(),
                    movie.getDirector(),
                    movie.getActors(),
                    movie.getGrade(),
                    movie.getFormats(),
                    genreIds,
                    genreNames
            );
        }).collect(Collectors.toList());
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public List<String> getAllGrades() {
        return movieRepository.findDistinctGrades();
    }

    public List<MovieDto> getMoviesByGrade(String grade) {
        return movieRepository.findByGrade(grade).stream().map(m -> {
            List<Long> genreIds = m.getGenres().stream()
                    .map(GenreEntity::getGenreId)
                    .collect(Collectors.toList());

            List<String> genreNames = m.getGenres().stream()
                    .map(GenreEntity::getGenreName)
                    .collect(Collectors.toList());

            return new MovieDto(
                    m.getMovieId(),
                    m.getTitle(),
                    m.getRunningTime(),
                    m.getReleaseDate(),
                    m.getDirector(),
                    m.getActors(),
                    m.getGrade(),
                    m.getFormats(),
                    genreIds,
                    genreNames
            );
        }).collect(Collectors.toList());
    }
}
