package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;


@Repository
@Primary
public class GenreDbStorage extends BaseStorage {

    public GenreDbStorage(JdbcTemplate jdbc, @Qualifier("genreMapper") RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    private static final String FIND_ALL_GENRES = "SELECT * FROM genre";
    private static final String FIND_GENRE = "SELECT * FROM genre WHERE genre_id = ?";

    // Получение списка всех жанров
    public Collection<Genre> getAllGenres() {
        return findMany(FIND_ALL_GENRES);
    }

    // Получение жанра по ID
    public Optional<Genre> getGenre(long id) {
        return findOne(FIND_GENRE, id);
    }
}


