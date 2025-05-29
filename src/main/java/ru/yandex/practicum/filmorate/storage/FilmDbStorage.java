package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultSetExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class FilmDbStorage extends BaseStorage implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, @Qualifier("filmMapper") RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private static final String CREATE_GENRE = "MERGE INTO FILM_GENRES(film_id, genre_id)" + "VALUES (?, ?)";
    private static final String CREATE_NEW_FILMS = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?) ;";
    private static final String UPDATE_FILMS = " UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?;";
    private static final String ADD_LIKE = """
            INSERT INTO likes (user_id, film_id)
            SELECT ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM likes WHERE user_id = ? AND film_id = ?)""";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String FIND_ALL_FILMS = """
            SELECT FILMS.*, MPA.*,  GENRE.*, FILM_GENRES.*
            FROM FILMS
            JOIN MPA ON FILMS.mpa_id = MPA.mpa_id
            LEFT JOIN FILM_GENRES ON FILMS.film_id = FILM_GENRES.film_id
            LEFT JOIN GENRE ON FILM_GENRES.genre_id = GENRE.genre_id;""";
    private static final String FIND_POPULAR_FILMS_SQL =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name AS mpa_name, g.genre_id, g.genre_name AS genre_name, " +
            "COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "WHERE (? IS NULL OR g.genre_id = ?) " +
            "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
            "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, g.genre_id, g.genre_name " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, m.mpa_id, m.mpa_name, g.genre_id, g.genre_name
            FROM Films f
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            ORDER BY fg.film_id ASC""";


    // Получение списка всех фильмов
    @Override
    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_FILMS);
    }

    // Создание фильма в таблице films
    @Override
    public Film create(Film film) {
        long id = insert(
                CREATE_NEW_FILMS,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        for (Genre genre : film.getGenres()) {
            jdbc.update(CREATE_GENRE, film.getId(), genre.getId());
        }
        return film;
    }

    // Обновление фильма
    @Override
    public Film update(Film newFilm) {
        update(UPDATE_FILMS,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        updateGenre(newFilm.getId());
        return newFilm;
    }

    // Обновление жанра фильма
    public Film updateGenre(long id) {
        return jdbc.query(GET_BY_ID_QUERY, new FilmResultSetExtractor(), id);
    }

    // Получение фильма по ID
    public Optional<Film> findById(long filmId) {
        return findOne(GET_BY_ID_QUERY, filmId);
    }

    // Получение фильма по ID со всеми жанрами
    public Film getById(long id) {
        return jdbc.query(GET_BY_ID_QUERY, new FilmResultSetExtractor(), id);
    }

    // Добавление в лайка фильму
    public void addLike(long userId, long filmId) {
        jdbc.update(ADD_LIKE, userId, filmId, userId, filmId);
    }

    //Удаление из лайка фильма
    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE, userId, filmId);
    }

    // Получение популярных фильмов
    public Collection<Film> getPopularFilms(Long count, Long genreId, Long year) {

        return findMany(FIND_POPULAR_FILMS_SQL,
                genreId, genreId,
                year, year,
                count
        );
    }

}