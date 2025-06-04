package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Feed {

    private Timestamp timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;

    public Feed() {
    }
}
