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

import java.util.*;

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
    private static final String ADD_LIKE = """
            INSERT INTO likes (user_id, film_id)
            SELECT ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM likes WHERE user_id = ? AND film_id = ?)""";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM FILM_GENRES WHERE film_id = ? AND genre_id = ?";
    private static final String DELETE_FILM_DIRECTOR = "DELETE FROM FILM_DIRECTORS WHERE film_id = ? AND director_id = ?";

    private static final String DELETE_FILM = """
            DELETE FROM FILM_GENRES WHERE film_id = ?;
            DELETE FROM LIKES WHERE film_id = ?;
            DELETE FROM films WHERE film_id = ?""";
    private static final String FIND_ALL_FILMS = """
            SELECT F.*, M.*,  g.*, fg.*, FD.*, D.*
            FROM FILMS f
            JOIN MPA m ON f.mpa_id = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON F.film_id = fg.film_id
            LEFT JOIN GENRE g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id
            ORDER BY  f.film_id, g.genre_id ASC ;""";
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
    private static final String FIND_POPULAR_FILMS_SQL =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name AS mpa_name, " +
            "g.genre_id, g.genre_name AS genre_name, d.director_id, d.director_name AS director_name, " +
            "COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
            "LEFT JOIN director d ON fd.director_id = d.director_id " +
            "WHERE (? IS NULL OR g.genre_id = ?) " +
            "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
            "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, g.genre_id, g.genre_name, d.director_id, d.director_name " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";
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

    private static final String UPDATE_GENRE = "Update FILM_GENRES SET genre_id = ? WHERE  film_id = ?;";
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
    private static final String GET_FILMS_SORT_BY_LIKES = """
            SELECT F.*, m.*, COUNT(fl.user_id) AS likes_count, g.*, fg.*, fd.*, d.*
            FROM Films f
            LEFT JOIN likes fl ON f.film_id = fl.film_id
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id
            WHERE d.director_id = ?
            GROUP BY f.film_id, f.name, G.GENRE_ID
            ORDER BY likes_count DESC;""";
    private static final String GET_FILMS_SORT_BY_RELEASE_DATE = """
            SELECT F.*, m.*, COUNT(fl.user_id) AS likes_count, g.*, fg.*, fd.*, d.*
            FROM Films f
            LEFT JOIN likes fl ON f.film_id = fl.film_id
            LEFT JOIN MPA m ON f.mpa_ID = m.mpa_id
            LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
            LEFT JOIN Genre g ON fg.genre_id = g.genre_id
            LEFT JOIN FILM_DIRECTORS FD ON f.film_id = fd.film_id
            LEFT JOIN DIRECTOR D ON fd.director_id = d.director_id
            WHERE d.director_id = ?
            GROUP BY f.film_id, f.name, G.GENRE_ID
            ORDER BY F.RELEASE_DATE ASC;""";

    private static final String GET_FILM_RECOMMENDATIONS = """
            SELECT l.FILM_ID
            FROM LIKES AS l
            WHERE l.USER_ID IN
             (SELECT ls.user_id
             FROM LIKES AS ll
             JOIN  LIKES AS ls ON (ll.FILM_ID = ls.FILM_ID )
             WHERE ll.USER_ID != ls.USER_ID AND ll.user_id =%d
             GROUP BY ls.user_id
             ORDER BY  count(ls.FILM_ID) DESC
             LIMIT 10)
            AND l.FILM_ID NOT IN
             (SELECT FILM_ID FROM LIKES WHERE USER_ID =%d);""";


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
        List<Genre> genres = new ArrayList<>(film.getGenres());
        genres.sort(Comparator.comparing(Genre::getId));

        for (Genre genre : film.getGenres()) {
            jdbc.update(CREATE_GENRE, film.getId(), genre.getId());
        }
        for (Director director : film.getDirectors()) {
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

        Film film = getById(newFilm.getId());

        List<Genre> genres = new ArrayList<>(newFilm.getGenres());
        genres.sort(Comparator.comparing(Genre::getId));

        for (Genre genre : film.getGenres()) {
            jdbc.update(DELETE_FILM_GENRES, film.getId(), genre.getId());
        }
        film.getGenres().clear();
        for (Genre genre : genres) {
            jdbc.update(CREATE_GENRE, newFilm.getId(), genre.getId());
        }

        for (Director director : film.getDirectors()) {
            jdbc.update(DELETE_FILM_DIRECTOR, film.getId(), director.getId());
        }
        film.getDirectors().clear();

        for (Director director : newFilm.getDirectors()) {
            jdbc.update(CREATE_DIRECTORS, newFilm.getId(), director.getId());
        }

        for (Genre genre : newFilm.getGenres()) {
            jdbc.update(UPDATE_GENRE, genre.getId(), newFilm.getId());
        }
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
        jdbc.update(ADD_LIKE, userId, filmId, userId, filmId);
    }

    //Удаление из лайка фильма
    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE, userId, filmId);

    }

    public void deleteFilm(long filmId) {
        jdbc.update(DELETE_FILM, filmId, filmId, filmId);

    }

    // Получение популярных фильмов
    public Collection<Film> getPopularFilms(Long count, Long genreId, Long year) {

        return findMany(FIND_POPULAR_FILMS_SQL,
                genreId, genreId,
                year, year,
                count
        );
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return getCommonFilms(GET_COMMON_FILMS, userId, friendId);
    }

    public Collection<Film> search(String query, Set<String> byParam) {
        if (byParam.size() == 2) {
            List<Film> find = findMany(GET_POPULAR_FILMS);
            return find.stream().filter(film -> film.getName().toLowerCase().contains(query)
                                                && film.getDirectors().stream().anyMatch(director -> director.getName().toLowerCase().contains(query)))
                    .toList();
        }
        if (byParam.size() == 1 && byParam.contains("director")) {
            List<Film> find = findMany(GET_POPULAR_FILMS);
            return find.stream().filter(film -> film.getDirectors().stream().anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase()))).toList();
        }
        List<Film> find = findMany(GET_POPULAR_FILMS);
        return find.stream().filter(film -> film.getName().toLowerCase().contains(query.toLowerCase())).toList();
    }

    //Список фильмов отсортированных по лайкам
    public List<Film> getFilmsSortByLikes(Long directorId) {
        return findMany(GET_FILMS_SORT_BY_LIKES, directorId);
    }

    //Список фильмов отсортированных по дате
    public List<Film> getFilmsSortByDate(Long directorId) {
        return findMany(GET_FILMS_SORT_BY_RELEASE_DATE, directorId);
    }


    public Collection<Long> getFilmRecommendations(Long userId) {
        return (jdbc.queryForList(String.format(GET_FILM_RECOMMENDATIONS, userId, userId), Long.class));
    }

}