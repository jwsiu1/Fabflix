DROP procedure IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie(IN movie_title varchar(100), IN movie_year INT, IN movie_director varchar(100), IN star_name varchar(100), IN genre_name varchar(32)) 
BEGIN
	DECLARE tempMovieID varchar(10);
    DECLARE tempStarID varchar(10);
    DECLARE tempGenreID int;
	SET tempMovieID = (SELECT max(id) FROM movies);
    SET @movie_id = concat("tt", cast(substring(tempMovieID, 3, 7) as unsigned) + 1);
    SET tempStarID = (SELECT max(id) FROM stars);
    SET @star_id = concat("nm", cast(substring(tempStarID, 3, 7) as unsigned) + 1);
    SET tempGenreID = (SELECT max(id) FROM genres);
    SET @genre_id = tempGenreID + 1;
    IF NOT EXISTS (SELECT * FROM movies WHERE title=movie_title AND year=movie_year AND director=movie_director) THEN
		INSERT INTO movies (id, title, year, director) VALUES (@movie_id, movie_title, movie_year, movie_director);
<<<<<<< HEAD
        INSERT INTO ratings (movieId, rating, numVotes) VALUES (@movie_id, 0, 0);
=======
                INSERT INTO ratings (movieId, rating, numVotes) VALUES (@movie_id, 0, 0);
>>>>>>> d94e22b9c93167f94e23270d8f4e6c93eff602ea
		IF NOT EXISTS (SELECT * FROM stars WHERE name=star_name) THEN
			INSERT INTO stars (id, name) VALUES (@star_id, star_name);
			INSERT INTO stars_in_movies (starId, movieID) VALUES (@star_id, @movie_id);
		ELSE 
			SET @star_id = (SELECT id FROM stars WHERE name=star_name LIMIT 1);
            INSERT INTO stars_in_movies (starId, movieId) VALUES (@star_id, @movie_id);
		END IF;
        IF NOT EXISTS (SELECT * FROM genres WHERE name=genre_name) THEN
			INSERT INTO genres (id, name) VALUES (@genre_id, genre_name);
            INSERT INTO genres_in_movies (genreId, movieId) VALUES (@genre_id, @movie_id);
		ELSE
			SET @genre_id = (SELECT id FROM genres WHERE name=genre_name LIMIT 1);
			INSERT INTO genres_in_movies (genreId, movieId) VALUES (@genre_id, @movie_id);
        END IF;
	ELSE
		SIGNAL SQLSTATE '45000' SET message_text = "movie already exists";
	END IF;

END $$ 
DELIMITER ;
