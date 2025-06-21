package com.example.moviebook.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "GENRE")
@Getter
@Setter
@NoArgsConstructor
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENRE_ID")
    private Long genreId;

    @Column(name = "GENRE_NAME", nullable = false)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @JsonManagedReference
    private List<MovieEntity> movies = new ArrayList<>();
}
