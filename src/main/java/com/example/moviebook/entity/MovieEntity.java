package com.example.moviebook.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

@Entity
@Table(name = "MOVIE")
@Getter
@Setter
@NoArgsConstructor
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOVIE_ID")
    private Long movieId;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "RUNNING_TIME")
    private int runningTime;

    @Column(name = "RELEASE_DATE")
    private LocalDate releaseDate;

    @Column(name = "DIRECTOR")
    private String director;

    @ElementCollection
    @CollectionTable(name = "MOVIE_CAST", joinColumns = @JoinColumn(name = "MOVIE_ID"))
    @Column(name = "ACTOR_NAME")
    private List<String> actors = new ArrayList<>();

    @Column(name = "GRADE")
    private String grade;

    @ElementCollection
    @CollectionTable(name = "MOVIE_FORMAT", joinColumns = @JoinColumn(name = "MOVIE_ID"))
    @Column(name = "FORMAT_NAME")
    private List<String> formats = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "MOVIE_GENRE",
            joinColumns = @JoinColumn(name = "MOVIE_ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_ID")
    )
    @JsonBackReference
    private List<GenreEntity> genres = new ArrayList<>();

    @Column(name = "POSTER_PATH", length = 500)
    private String posterPath;
}

