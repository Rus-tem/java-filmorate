package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enumFeed.eventType;
import ru.yandex.practicum.filmorate.model.enumFeed.operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

    private Long timestamp;
    private Long userId;
    private eventType eventType;
    private operation operation;
    private Long eventId;
    private Long entityId;

}

