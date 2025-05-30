package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultSetExtractor;

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
    private static final String ADD_LIKE = "MERGE INTO LIKES(film_id, user_id) VALUES (?, ?);";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String DELETE_FILM = """
            DELETE FROM FILM_GENRES WHERE film_id = ?;
            DELETE FROM LIKES WHERE film_id = ?;
            DELETE FROM films WHERE film_id = ?""";
    private static final String FIND_ALL_FILMS = """
            SELECT FILMS.*, MPA.*,  GENRE.*, FILM_GENRES.*
            FROM FILMS
            JOIN MPA ON FILMS.mpa_id = MPA.mpa_id
            LEFT JOIN FILM_GENRES ON FILMS.film_id = FILM_GENRES.film_id
            LEFT JOIN GENRE ON FILM_GENRES.genre_id = GENRE.genre_id;""";
    private static final String GET_POPULAR_FILMS = """
            SELECT F.*, m.*, COUNT(fl.user_id) AS likes_count, g.*, fg.*
            FROM Films f
            LEFT JOIN likes fl ON f.film_id = fl.film_id
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            GROUP BY f.film_id, f.name, G.GENRE_ID
            ORDER BY likes_count DESC;""";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, m.mpa_id, m.mpa_name, g.genre_id, g.genre_name
            FROM Films f
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            ORDER BY fg.film_id ASC""";
    private static final String GET_COMMON_FILMS = """
            SELECT f.film_id, f.name AS film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, g.genre_id, g.genre_name, COUNT(l.user_id) AS likes_count
            FROM films f
            JOIN mpa m ON f.mpa_id = m.mpa_id
            JOIN film_genres fg ON f.film_id = fg.film_id
            JOIN genre g ON fg.genre_id = g.genre_id
            JOIN likes l ON f.film_id = l.film_id
            WHERE l.film_id IN (SELECT DISTINCT l1.film_id FROM likes l1 WHERE l1.user_id = ?)
                AND l.film_id IN (
                    SELECT DISTINCT l2.film_id
                    FROM likes l2
                    WHERE l2.user_id = ?
                )
            GROUP BY f.film_id, f.name
            ORDER BY likes_count DESC;
            """;


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
        jdbc.update(ADD_LIKE, userId, filmId);
    }

    //Удаление из лайка фильма
    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE, userId, filmId);

    }

    public void deleteFilm(long filmId) {
        jdbc.update(DELETE_FILM, filmId, filmId, filmId);

    }

    // Получение популярных фильмов
    public List<Film> getPopularFilms() {
        return findMany(GET_POPULAR_FILMS);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return getCommonFilms(GET_COMMON_FILMS, userId, friendId);
    }

}