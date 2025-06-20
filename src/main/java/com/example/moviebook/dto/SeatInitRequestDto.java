package com.example.moviebook.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeatInitRequestDto {
    private List<List<Integer>> layout;

    public List<List<Integer>> getLayout() {
        return layout;
    }

    public void setLayout(List<List<Integer>> layout) {
        this.layout = layout;
    }
}
