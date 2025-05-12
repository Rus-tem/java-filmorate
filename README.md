# java-filmorate
Template repository for Filmorate project.
<picture>
 <source media="(prefers-color-scheme: dark)"[ srcset="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
 <source media="(prefers-color-scheme: light)" srcset="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
 <img alt="YOUR-ALT-TEXT" src="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
</picture>
<H3> Диаграмма описывает 9 таблиц: </H3>
<h2>Films</h2>
 Номер (id) фильма;
 Название (name) фильма;
 Описание (description) фильма;
 Дата создания (releaseDate) фильма;
 Длительность (duration) фильма;
 Жанр (genre) фильма - ссылка на таблицу genre c названиями жанра фильма;
 Возрастной рейтинг (ageRating) фильма;
 Список лайков, содержит id User - ссылка на таблицу likesIdUser со списком id.
</H3>
 <h2>User</h2>
 <H3>
 Номер (id) пользователя в таблице;
 Логин (login) пользователя;
 Имя (name) пользователя;
 Электронный ящик (email) пользователя;
 День рождения (birthday) пользователя;
 Список друзей (friendsId) пользователя - ссылка на таблицу friendsId со списком id друзей и статусом (подтвержденная, не подтвержденная).
</H3>
 <h2>getAllFilms</h2>
 <H3> Получение списка всех фильмов. Содержит ссылки на id всех фильмов</H3>
 <h2>getPopularFilms</h2>
<H3> 
 Получение списка популярных фильмов(фильмы у которых есть лайки)</H3>
 Содержит id фильма и количество лайков у фильма 
 </H3>
  <h2>getAllUsers</h2>
<H3>   Получение списка всех пользователей. Содержит ссылки на id все пользователей</H3>
   <h2>getCommonFriends</h2>
 <H3>  Получение списка общих друзей 2 пользователей. Содержит id пользователя и список id его друзей </H3>
  <h2>getAllUsers</h2>
<H3>   Получение списка всех пользователей. Содержит ссылки на id все пользователей</H3>
   <h2>getCommonFriends</h2>
 <H3>  Получение списка общих друзей 2 пользователей. Содержит id пользователя и список id его друзей </H3>
