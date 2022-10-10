package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Review {

    private int reviewId;

    @NotNull
    private final String content;

    @NotNull
    private  Boolean isPositive;

    @NotNull
    private Integer userId;

    @NotNull
    private final Integer filmId;

    private int useful;
}
