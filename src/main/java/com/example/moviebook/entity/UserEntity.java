package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String password;
    private String gender;
    private String birthDate;
    private String phone;
}
