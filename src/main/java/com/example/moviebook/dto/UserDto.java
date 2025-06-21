package com.example.moviebook.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long id;

    private String name;
    private String email;
    private String password;
    private String gender;
    private String birthDate;
    private String phone;
    private int money;
}
