package com.comsysto.movie.service.impl;

import com.comsysto.movie.repository.model.Movie;
import com.comsysto.movie.repository.query.MovieQuery;
import com.comsysto.movie.service.api.MovieService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.*;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 5/29/13
 * Time: 4:40 PM
 */
@ContextConfiguration(locations = "classpath:com/comsysto/movie/service/spring-context.xml")
@ActiveProfiles("default")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MovieServiceImplTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    MovieService movieService;

    @Test
    public void testCreateFindAndDeleteMovie() {
        movieService.deleteAll();

        Movie movie = Movie.MovieBuilder.create("Movie").build();
        movieService.save(movie);
        List<Movie> retrievedMovies = movieService.findAll();

        assertEquals(1, retrievedMovies.size());
        assertEquals(movie.getTitle(), retrievedMovies.get(0).getTitle());

        movieService.delete(retrievedMovies.get(0));
        retrievedMovies = movieService.findAll();
        assertEquals(0, retrievedMovies.size());
    }

    @Test
    public void testImportMovies() {
        movieService.importMovies(5, true);

        List<Movie> retrievedMovies = movieService.findAll();
        assertEquals(5, retrievedMovies.size());

        movieService.deleteAll();
    }

    @Ignore // takes too long, only used for importing data
    @Test
    public void testLargeImportMovies() {
        movieService.deleteAll();
        movieService.importMovies(10000, true);
        List<Movie> retrievedMovies = movieService.findAll();
        assertEquals(10000, retrievedMovies.size());
    }

    @Test
    public void testFindByQuery() {
        movieService.deleteAll();

        Movie movie1 = randomMovie("I am Searching", true);
        Movie movie2 = randomMovie("", false);
        Movie movie3 = randomMovie("I am Searching", false);
        Movie movie4 = randomMovie("", true);

        movieService.save(movie1);
        movieService.save(movie2);
        movieService.save(movie3);
        movieService.save(movie4);

        MovieQuery query = MovieQuery.MovieQueryBuilder.create()
                .withDescriptionNoFullTextSearch("Searching")
                .withAlreadyWatched(true)
                .build();

        List<Movie> queryResult = movieService.findByQuery(query);

        assertEquals(1, queryResult.size());
        assertTrue(queryResult.get(0).getTitle().equals(movie1.getTitle()));

        movieService.deleteAll();
    }

    @Test
    public void testFindByQueryFullTextSearch() {
        movieService.deleteAll();

        Movie movie1 = randomMovie("I am Searching", true);
        Movie movie2 = randomMovie("", false);
        Movie movie3 = randomMovie("I am Searching", false);
        Movie movie4 = randomMovie("", true);

        movieService.save(movie1);
        movieService.save(movie2);
        movieService.save(movie3);
        movieService.save(movie4);

        MovieQuery query = MovieQuery.MovieQueryBuilder.create()
                .withDescriptionFullTextSearch("search") // make sure stemming works
                .withAlreadyWatched(true)
                .build();

        List<Movie> queryResult = movieService.findByQuery(query);

        assertEquals(1, queryResult.size());
        assertTrue(queryResult.get(0).getTitle().equals(movie1.getTitle()));

        movieService.deleteAll();
    }

    private Movie randomMovie(String partOfDescription, boolean alreadyWatched) {
        Random random = new Random();
        return Movie.MovieBuilder.create(" Random "+new BigInteger(40, random).toString(32))
                .withDescription(new BigInteger(2000, random).toString(32)+" "
                        +partOfDescription+" "+new BigInteger(2000, random).toString(32))
                .withAlreadyWatched(alreadyWatched)
                .build();
    }
}
