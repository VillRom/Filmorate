package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface Event {
    List<FeedEvent> getEventById (long userId);

    void addEvent(long userId, String eventType, String operation, long entityId);
}
