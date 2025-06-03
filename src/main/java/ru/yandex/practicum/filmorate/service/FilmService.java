package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final UserDbStorage userDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserDbStorage userDbStorage, FilmDbStorage filmDbStorage, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage, DirectorDbStorage directorDbStorage, UserService userService) {

        this.filmDbStorage = filmDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.directorDbStorage = directorDbStorage;
        this.userDbStorage = userDbStorage;
        this.userService = userService;
    }

    // Получение всех из таблицы films
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmDbStorage.getAllFilms());
    }

    // public Collection<Film> getAllFilmsTest() {
    //     Set<Film> listAllFilms = new HashSet<>();
    //     List<Film> filmsId = filmDbStorage.getAllFilms();
    //     for (Film filmId : filmsId) {
    //         Film film = filmDbStorage.getById(filmId.getId());
    //          listAllFilms.add(film);
    //       }
    //       return listAllFilms.stream().toList().reversed();
//    }


    // Создание фильма в таблице films
    public Film createFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            Optional<Genre> optionalGenre = genreDbStorage.getGenre(genre.getId());
            if (optionalGenre.isEmpty()) {
                throw new NotFoundException("Genre не найден");
            }
        }
        Optional<MPA> optionalMpa = mpaDbStorage.getMpa(film.getMpa().getId());
        if (optionalMpa.isEmpty()) {
            throw new NotFoundException("MPA не найден");
        }
        return filmDbStorage.create(film);
    }

    // Обновление фильма в таблице films
    public Film updateFilm(Film updateFilm) {
        filmDbStorage.update(updateFilm);
        return updateFilm;
    }

    public Film deleteFilm(Long deleteFilm) {
        if (deleteFilm == null || deleteFilm <= 0) {
            throw new ValidationException("Некорректный ID");
        }
        if (filmDbStorage.getById(deleteFilm) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        Film film = filmDbStorage.getById(deleteFilm);
        filmDbStorage.deleteFilm(deleteFilm);
        return film;
    }

    // Добавление лайка фильму
    public Film addLike(Long filmId, Long userId) {
        filmDbStorage.addLike(filmId, userId);
        userService.addFeed(userId, "LIKE", "ADD", filmId);
        return filmDbStorage.findById(filmId).get();
    }

    // Удаление из лайка фильма
    public Film deleteLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 0) {
            throw new NotFoundException("Такой фильм не существует");
        }
        if (userId == null || userId < 0) {
            throw new NotFoundException("Такой пользователь не существует");
        }
        filmDbStorage.deleteLike(filmId, userId);
        userService.addFeed(userId, "LIKE", "REMOVE", filmId);
        return filmDbStorage.findById(filmId).get();
    }

    // Получение списка отмеченных лайком (список популярных фильмов)
    public Collection<Film> getPopularFilms(Long count, Long genreId, Long year) {
        if (count != null && count < 0) {
            throw new ValidationException("Количество фильмов не может быть отрицательным");
        }
        if (genreId != null && (genreId <= 0 || genreId > 6)) {
            throw new ValidationException("Жанр должен быть от 1 до 6");
        }
        if (year != null && year < 1895) {
            throw new ValidationException("Год выпуска фильма не может быть раньше 1895");
        }

        Long actualCount = count == null || count == 0 ? 10L : count;

        return filmDbStorage.getPopularFilms(actualCount, genreId, year);
    }

    public Collection<Film> search(String query, String by) {
        if (query == null || query.isBlank() || by.isBlank()) {
            throw new ValidationException("Пустой запрос");
        }
        Set<String> byParam = Arrays.stream(by.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        if (!byParam.contains("title") && !byParam.contains("director")) {
            throw new ValidationException("Неправильный параметр");
        }

        return filmDbStorage.search(query, byParam);
    }


    // Получение фильма по ID
    public Film getFilmWithId(Long filmId) {
        Film film = filmDbStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    // Получение всех жанров
    public Collection<Genre> getAllGenres() {
        return new ArrayList<>(genreDbStorage.getAllGenres());
    }

    // Получение жанра по ID
    public Genre getGenre(Long id) {
        Optional<Genre> optionalGenre = genreDbStorage.getGenre(id);
        if (optionalGenre.isEmpty()) {
            throw new NotFoundException("Жанр не найден");
        }
        return optionalGenre.get();
    }

    // Получение всех MPA
    public Collection<MPA> getAllMpa() {
        return new ArrayList<>(mpaDbStorage.getAllMpa());
    }

    // Получение MPA по ID
    public MPA getMpa(Long id) {
        Optional<MPA> optionalMpa = mpaDbStorage.getMpa(id);
        if (optionalMpa.isEmpty()) {
            throw new NotFoundException("MPA не найден");
        }
        return optionalMpa.get();
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId == friendId || userId < 0 || friendId < 0 || userId == 0 || friendId == 0) {
            throw new ValidationException("Не корректные данные о пользователях");
        }
        if (userDbStorage.findById(userId).isEmpty() || userDbStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return filmDbStorage.getCommonFilms(userId, friendId);
    }

    // Получение списка всех режиссеров(directors)
    public Collection<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    // Получение режиссера(director) по ID
    public Director getDirector(long id) {
        Optional<Director> optionalDirector = directorDbStorage.getDirector(id);
        if (optionalDirector.isEmpty()) {
            throw new NotFoundException("Режиссер не найден");
        }
        return optionalDirector.get();
    }

    //Создание режиссера(director)
    public Director createDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new NullFoundException("Не корректное имя режиссера");
        }
        return directorDbStorage.createDirector(director);
    }

    // Обновление режиссера (director)
    public Director uptadeDirector(Director director) {
        return directorDbStorage.uptadeDirector(director);
    }

    //Удаление режиссера(director) по ID
    public void deleteDirector(Long id) {
        directorDbStorage.deleteDirector(id);
    }

    // Получение фильма отсортированного по дате или лайкам
    public Collection<Film> getFilmSortByLikesOrYears(Long directorId, String sortBy) {
        if (getDirector(directorId) == null) {
            throw new NotFoundException("Режиссер не найден");
        }
        if (sortBy.equals("likes")) {
            return filmDbStorage.getFilmsSortByLikes(directorId);
        } else if (sortBy.equals("year")) {
            return filmDbStorage.getFilmsSortByDate(directorId);
        } else {
            throw new ValidationException("Указан неправильный тип сортировки");
        }
    }


    // Получение списка рекомендованных фильмов

    public Collection<Film> getFilmRecommendations(Long userId) {
        if (userId == null || userId < 0 || userId == 0) {
            throw new ValidationException("не правильный id пользователя");
        }

        Collection<Long> filmsRecommendations = filmDbStorage.getFilmRecommendations(userId);
        if (filmsRecommendations.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> filmsRecommend = new HashSet<>(filmDbStorage.getFilmRecommendations(userId));

        return filmsRecommend.stream().map(this::getFilmWithId).collect(Collectors.toList());
    }
}
