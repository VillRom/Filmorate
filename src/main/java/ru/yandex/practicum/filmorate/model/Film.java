package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private final Set<Long> likes = new HashSet<>();
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();

    private final Set<Director> directors = new HashSet<>();

    public Film(long id, String name, LocalDate releaseDate, String description, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.description = description;
        this.duration = duration;
        this.mpa = mpa;
    }
    public void setLikes(Set<Long> like) {
        likes.addAll(like);
    }

    public void setGenres(List<Genre> genreList) {
        genreList.sort(new Comparator<Genre>() {
            @Override
            public int compare(Genre o1, Genre o2) {
                return o1.getId() - o2.getId();
            }});
        genres.clear();
        genres.addAll(genreList);
    }

    public void setDirector(List<Director> directorList) {
        directors.clear();
        directors.addAll(directorList);
    }
}

