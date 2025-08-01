package com.example.moviebook.service;

import com.example.moviebook.entity.GenreEntity;
import com.example.moviebook.dto.GenreDto;
import com.example.moviebook.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    // 장르 등록
    public GenreDto registerGenre(GenreDto dto) {
        if(genreRepository.existsByGenreName(dto.getGenreName())) {
            throw new IllegalArgumentException("이미 존재하는 장르입니다.");
        }

        GenreEntity genre = new GenreEntity();
        genre.setGenreName(dto.getGenreName());

        GenreEntity savedGenre = genreRepository.save(genre);
        return new GenreDto(
                savedGenre.getGenreId(),
                savedGenre.getGenreName(),
                null);
    }

    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre -> new GenreDto(
                        genre.getGenreId(),
                        genre.getGenreName(),
                        genre.getMovies().stream()
                                .map(movie -> new GenreDto.MovieTitleDto(movie.getMovieId(), movie.getTitle()))
                                .toList()
                ))
                .toList();
    }
}