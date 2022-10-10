package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public class Director {
    private long id;
    @NotBlank
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
