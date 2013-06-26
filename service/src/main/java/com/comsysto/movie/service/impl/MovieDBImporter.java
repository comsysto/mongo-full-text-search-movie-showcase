package com.comsysto.movie.service.impl;

import com.comsysto.movie.repository.api.MovieRepository;
import com.comsysto.movie.repository.model.Movie;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 6/12/13
 * Time: 2:36 PM
 */
@Service
public class MovieDBImporter {

    // API Key
//    private static final String API_KEY = "16a0036a641140ce7e23ddd423dfbf50"; TODO don't publish this one!
    private static final String API_KEY = "5a1a77e2eba8984804586122754f969f";

    @Autowired
    MovieRepository movieRepository;

    Random rnd = new Random();

    public void importMovies (int numberOfMovies) throws IOException {
        int counter = 0;

        int numPages = numberOfMovies/20+1;
        for (int page=1; page<=numPages; page++) {
            URL url = new URL("http://api.themoviedb.org/3/discover/movie?api_key="+API_KEY+"&page="+page);
            InputStream is = url.openStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int retVal;
            while ((retVal = is.read()) != -1) {
                os.write(retVal);
            }

            final String movieString = os.toString();
            BasicDBObject parsedResponse = (BasicDBObject) JSON.parse(movieString);
            List<DBObject> movieList = (List<DBObject>) parsedResponse.get("results");

            if (movieList.isEmpty()) {
                break;
            }

            for (DBObject movieObject : movieList) {
                if (counter>=numberOfMovies) {
                    break;
                }

                String movieId = movieObject.get("id").toString();
                try {
                    Movie movie = importMovieById(movieId);
                    movieRepository.save(movie);
                    counter++;
                } catch (IOException e) {
                    System.out.println("problem with importing a certain movie...");
                    System.out.println(e);
                    System.out.println("trying next one...");
                }
            }
        }
    }

    private Movie importMovieById(String movieId) throws IOException {
        URL url = new URL("http://api.themoviedb.org/3/movie/"+ movieId +"?api_key="+API_KEY);
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int retVal;
        while ((retVal = is.read()) != -1) {
            os.write(retVal);
        }

        final String movieString = os.toString();
        BasicDBObject movieObject = (BasicDBObject) JSON.parse(movieString);

        String title = movieObject.get("title").toString();
        Object overviewObject = movieObject.get("overview");
        String yearString = movieObject.get("release_date").toString().split("-")[0];

        String description = "";
        if (overviewObject != null) {
            description = overviewObject.toString();
        }

        int year = 0;
        if (yearString.length() >0) {
            year = Integer.valueOf(yearString);
        }

        Movie.MovieBuilder movieBuilder = Movie.MovieBuilder.create(title)
                .withDescription(description)
                .withYear(year);

        if (rnd.nextBoolean()) {
            movieBuilder.withAlreadyWatched(true);
        }
        else if (rnd.nextBoolean()) {
            movieBuilder.withLikeToWatch(true);
        }

        Movie movie = movieBuilder.build();
        return movie;
    }

}
