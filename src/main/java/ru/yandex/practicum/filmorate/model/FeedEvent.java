package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@Getter
@Setter
public class FeedEvent {

    private long eventId;

    private long timestamp;

    private long userId;

    private String eventType;

    private String operation;

    private long entityId;
}
