package com.comsysto.movie.service.impl;

import com.comsysto.movie.repository.api.MovieRepository;
import com.comsysto.movie.repository.model.Movie;
import com.comsysto.movie.repository.query.MovieQuery;
import com.comsysto.movie.service.api.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 5/29/13
 * Time: 4:37 PM
 */
@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MovieDBImporter movieDBImporter;

    @Override
    public long countAll() {
        return movieRepository.countAll();
    }

    @Override
    public long countForQuery(MovieQuery query) {
        long startTime = System.currentTimeMillis();
        long result = movieRepository.countForQuery(query);
        long durationMillis = System.currentTimeMillis() - startTime;

        printDebugString("count "+result+":\t", query, durationMillis);
        return result;
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> findByQuery(MovieQuery query) {
        long startTime = System.currentTimeMillis();
        List<Movie> result = movieRepository.findByQuery(query);
        long durationMillis = System.currentTimeMillis() - startTime;

        printDebugString("retrieve:\t",query, durationMillis);
        return result;
    }

    private void printDebugString(String prefix, MovieQuery query, long durationMillis) {
        String debugString = "executed query ";
        if (query.getDescriptionFullTextSearch() != null) {
            debugString += "with full-text-search ";
        }
        else if (query.getDescriptionNoFullTextSearch() != null) {
            debugString += "with non-full-text-search ";
        }
        else {
            return;
        }
        debugString += "in "+durationMillis+"ms...";
        System.out.println(prefix + debugString);
    }

    @Override
    public void save(Movie object) {
        movieRepository.save(object);
    }

    @Override
    public void delete(Movie object) {
        movieRepository.delete(object);
    }

    @Override
    public void deleteAll() {
        movieRepository.removeAll();
    }

    @Override
    public void importMovies (int numberOfMovies, boolean dropOldEntries) {
        try {
            if (dropOldEntries) {
                movieRepository.dropCollection();
            }
            movieDBImporter.importMovies(numberOfMovies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
