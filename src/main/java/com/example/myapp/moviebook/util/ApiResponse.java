package com.example.moviebook.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean result;
    private String message;
    private T data;
}
