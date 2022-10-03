package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @Autowired
    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return service.get(id);
    }

    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(defaultValue = "0") int filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        return service.getForFilm(filmId, count);
    }

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        return service.add(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        service.addDislike(id,userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        service.removeDislike(id, userId);
    }




}
