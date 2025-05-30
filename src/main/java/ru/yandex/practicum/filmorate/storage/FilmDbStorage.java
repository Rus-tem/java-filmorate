package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultSetExtractorDirectors;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class FilmDbStorage extends BaseStorage implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, @Qualifier("filmMapper") RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }
    private static final String CREATE_DIRECTORS = "MERGE INTO FILM_DIRECTORS(film_id, director_id)" + "VALUES (?, ?)";
    private static final String CREATE_GENRE = "MERGE INTO FILM_GENRES(film_id, genre_id)" + "VALUES (?, ?)";
    private static final String CREATE_NEW_FILMS = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?) ;";
    private static final String UPDATE_FILMS = """
     UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
     WHERE film_id = ?;""";
    private static final String UPDATE_GENRE = "Update FILM_GENRES SET genre_id = ? WHERE  film_id = ?;";
    private static final String UPDATE_DIRECTOR = "Update FILM_DIRECTORS SET director_id = ? WHERE  film_id = ?;";
    private static final String ADD_LIKE = "MERGE INTO LIKES(film_id, user_id) VALUES (?, ?);";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String FIND_ALL_FILMS = """
            SELECT F.*, M.*,  g.*, fg.*, FD.*, D.*
            FROM FILMS f
            JOIN MPA m ON f.mpa_id = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON F.film_id = fg.film_id
            LEFT JOIN GENRE g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id;""";
    private static final String GET_POPULAR_FILMS = """
            SELECT F.*, m.*, COUNT(fl.user_id) AS likes_count, g.*, fg.*, fd.*, d.*
            FROM Films f
            LEFT JOIN likes fl ON f.film_id = fl.film_id
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id
            GROUP BY f.film_id, f.name, G.GENRE_ID
            ORDER BY likes_count DESC;""";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, m.*, g.*, d.*, fd.*
            FROM Films f
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id
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
        for (Director director: film.getDirectors()) {
            jdbc.update(CREATE_DIRECTORS, film.getId(), director.getId());
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
        for (Genre genre : newFilm.getGenres()) {
            jdbc.update(UPDATE_GENRE, genre.getId(), newFilm.getId());
        }
        for (Director director : newFilm.getDirectors()) {
            jdbc.update(UPDATE_DIRECTOR, director.getId(), newFilm.getId());
        }

       // updateGenre(newFilm.getId()); ???
       // updateDirector(newFilm.getId()); ???
        return newFilm;
    }

    // Обновление жанра фильма
    public Film updateGenre(long id) {
        return jdbc.query(GET_BY_ID_QUERY, new FilmResultSetExtractor(), id);
    }

    // Обновление режиссера(director) фильма
    public Film updateDirector(long id) {
        return jdbc.query(GET_BY_ID_QUERY, new FilmResultSetExtractorDirectors(), id);
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
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    // Получение популярных фильмов
    public List<Film> getPopularFilms() {
        return findMany(GET_POPULAR_FILMS);
    }

}