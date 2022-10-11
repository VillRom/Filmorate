package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
@Getter
public class Genre {

    private final Integer id;

    private final String name;
}
