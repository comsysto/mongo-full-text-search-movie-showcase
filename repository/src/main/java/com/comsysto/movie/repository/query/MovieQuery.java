package com.comsysto.movie.repository.query;

import org.springframework.data.domain.Sort;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 6/5/13
 * Time: 11:22 AM
 */
public class MovieQuery {

    private String descriptionNoFullTextSearch;
    private String descriptionFullTextSearch;
    private Integer year;
    private Boolean alreadyWatched;
    private Boolean likeToWatch;

    private long offset;
    private long limit;
    private Sort sort;

    private MovieQuery() {};

    public String getDescriptionNoFullTextSearch() {
        return descriptionNoFullTextSearch;
    }

    public String getDescriptionFullTextSearch() {
        return descriptionFullTextSearch;
    }

    public Integer getYear() {
        return year;
    }

    public Boolean isAlreadyWatched() {
        return alreadyWatched;
    }

    public Boolean isLikeToWatch() {
        return likeToWatch;
    }

    public long getOffset() {
        return offset;
    }

    public long getLimit() {
        return limit;
    }

    public Sort getSort() {
        return sort;
    }

    public static class MovieQueryBuilder {
        private MovieQuery query;

        private MovieQueryBuilder() {
            query = new MovieQuery();
        }

        public static MovieQueryBuilder create() {
            return new MovieQueryBuilder();
        }

        public MovieQueryBuilder withDescriptionNoFullTextSearch(String searchString) {
            query.descriptionNoFullTextSearch = searchString;
            return this;
        }

        public MovieQueryBuilder withDescriptionFullTextSearch(String searchString) {
            query.descriptionFullTextSearch = searchString;
            return this;
        }

        public MovieQueryBuilder withYear(int year) {
            query.year = year;
            return this;
        }

        public MovieQueryBuilder withAlreadyWatched(boolean alreadyWatched) {
            query.alreadyWatched = alreadyWatched;
            return this;
        }

        public MovieQueryBuilder withLikeToWatch(boolean likeToWatch) {
            query.likeToWatch = likeToWatch;
            return this;
        }

        public MovieQueryBuilder withPagination(long offset, long limit) {
            query.offset = offset;
            query.limit = limit;
            return this;
        }

        public MovieQueryBuilder withSort(Sort sort) {
            query.sort = sort;
            return this;
        }

        public MovieQuery build() {
            return query;
        }
    }
}
