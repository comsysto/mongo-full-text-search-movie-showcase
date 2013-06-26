package com.comsysto.movie.repository.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 5/29/13
 * Time: 4:12 PM
 */
@Document(collection = Movie.COLLECTION_NAME)
public class Movie implements Serializable {
    public static final String COLLECTION_NAME = "movie";

    @Id
    private ObjectId id;
    @Indexed
    private String title;
    @Indexed
    private String description;
    @Indexed
    private int year;
    @Indexed
    private boolean alreadyWatched;
    @Indexed
    private boolean likeToWatch;

    private Movie() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAlreadyWatched() {
        return alreadyWatched;
    }

    public void setAlreadyWatched(boolean alreadyWatched) {
        this.alreadyWatched = alreadyWatched;
    }

    public boolean isLikeToWatch() {
        return likeToWatch;
    }

    public void setLikeToWatch(boolean likeToWatch) {
        this.likeToWatch = likeToWatch;
    }

    public static class MovieBuilder {

        private Movie movie = new Movie();

        private MovieBuilder(String title) {
            movie.title = title;
        };

        public static MovieBuilder create(String title) {
            return new MovieBuilder(title);
        }

        public MovieBuilder withDescription(String description) {
            movie.description = description;
            return this;
        }

        public MovieBuilder withYear(int year) {
            movie.year = year;
            return this;
        }

        public MovieBuilder withAlreadyWatched(boolean flag) {
            movie.alreadyWatched = flag;
            return this;
        }

        public MovieBuilder withLikeToWatch(boolean flag) {
            movie.likeToWatch = flag;
            return this;
        }

        public Movie build() {
            return movie;
        }

    }
}
