package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
public class Genre {

    private final Integer id;

    private final String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
