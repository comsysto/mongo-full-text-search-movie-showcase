package com.comsysto.movie.repository.impl;

import com.comsysto.movie.repository.query.MovieQuery;
import com.comsysto.movie.repository.api.MovieRepository;
import com.comsysto.movie.repository.model.Movie;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 5/29/13
 * Time: 4:30 PM
 */
@Repository
public class MovieRepositoryImpl implements MovieRepository {

    @Autowired
    private MongoOperations mongoOperations;
    private Class<Movie> clazz = Movie.class;

    @PostConstruct
    public void ensureTextIndex() {
        // make sure the index is set up properly (not yet possible via Spring Data Annotations)
        mongoOperations.getCollection(Movie.COLLECTION_NAME).ensureIndex(new BasicDBObject("description", "text"));
    }

    @Override
    public void save(Movie entity) {
        mongoOperations.save(entity);
    }

    @Override
    public void delete(Movie entity) {
        mongoOperations.remove(entity);
    }

    @Override
    public void removeAll() {
        mongoOperations.remove(new Query(), clazz);
    }

    @Override
    public boolean collectionExists() {
        return mongoOperations.collectionExists(clazz);
    }

    @Override
    public void dropCollection() {
        mongoOperations.dropCollection(clazz);
        // make sure text index is present again after dropping
        ensureTextIndex();
    }

    @Override
    public long countAll() {
        return mongoOperations.count(new Query(), clazz);
    }

    @Override
    public long countForQuery(MovieQuery query) {
        Criteria criteria = mapSimpleQueryCriteria(query);
        if (query.getDescriptionFullTextSearch() != null) {
            CommandResult commandResult = executeFullTextSearch(query.getDescriptionFullTextSearch(), criteria);
            return extractSearchResultCount(commandResult);
        }
        else {
            return mongoOperations.count(Query.query(criteria), clazz);
        }
    }

    @Override
    public List<Movie> findAll() {
        return mongoOperations.findAll(clazz);
    }

    @Override
    public List<Movie> findByQuery(MovieQuery query) {
        Criteria criteria = mapSimpleQueryCriteria(query);
        Query mongoQuery;
        if (query.getDescriptionFullTextSearch() != null) {
            CommandResult commandResult = executeFullTextSearch(query.getDescriptionFullTextSearch(), criteria);
            Collection<ObjectId> searchResultIds = extractSearchResultIds(commandResult);
            mongoQuery = Query.query(Criteria.where("_id").in(searchResultIds));
        }
        else {
            mongoQuery = Query.query(criteria);
        }
        applySortAndPagination(query, mongoQuery);
        return mongoOperations.find(mongoQuery, clazz);
    }

    private Criteria mapSimpleQueryCriteria(MovieQuery query) {
        Criteria criteria = null;

        if (query.getDescriptionNoFullTextSearch() != null) {
            criteria = Criteria.where("description").regex(query.getDescriptionNoFullTextSearch());
        }
        if (query.getYear() != null) {
            criteria = updateCriteria(criteria, "year", query.getYear());
        }
        if (query.isAlreadyWatched() != null) {
            criteria = updateCriteria(criteria, "alreadyWatched", query.isAlreadyWatched());
        }
        if (query.isLikeToWatch() != null) {
            criteria = updateCriteria(criteria, "likeToWatch", query.isLikeToWatch());
        }

        if (criteria == null) {
            // use dummy in that case
            criteria = Criteria.where("title").exists(true);
        }
        return criteria;
    }

    private Criteria updateCriteria(Criteria criteria, String key, Object value) {
        if (criteria == null) {
            criteria = Criteria.where(key).is(value);
        }
        else {
            criteria.and(key).is(value);
        }
        return criteria;
    }

    private CommandResult executeFullTextSearch(String searchString, Criteria filterCriteria) {
        BasicDBObject textSearch = new BasicDBObject();
        textSearch.put("text", Movie.COLLECTION_NAME);
        textSearch.put("search", searchString);
        textSearch.put("filter", Query.query(filterCriteria).getQueryObject());
        textSearch.put("limit", countAll());
        textSearch.put("project", new BasicDBObject("_id", 1));
        return mongoOperations.executeCommand(textSearch);
    }

    private Collection<ObjectId> extractSearchResultIds(CommandResult commandResult) {
        Set<ObjectId> objectIds = new HashSet<ObjectId>();
        BasicDBList resultList = (BasicDBList) commandResult.get("results");
        if (resultList == null) {
            System.out.println(mongoFullTextSearchErrorMessage());
            return objectIds;
        }
        Iterator<Object> it = resultList.iterator();
        while (it.hasNext()) {
            BasicDBObject resultContainer = (BasicDBObject) it.next();
            BasicDBObject resultObject = (BasicDBObject) resultContainer.get("obj");
            ObjectId resultId = (ObjectId) resultObject.get("_id");
            objectIds.add(resultId);
        }
        return objectIds;
    }

    private long extractSearchResultCount(CommandResult commandResult) {
        BasicDBObject statsContainer = (BasicDBObject) commandResult.get("stats");
        if (statsContainer == null) {
            System.out.println(mongoFullTextSearchErrorMessage());
            return 0;
        }
        return (Integer) statsContainer.get("nfound");
    }

    private Query applySortAndPagination(MovieQuery query, Query mappedQuery) {
        if (query.getOffset() != 0) {
            mappedQuery.skip((int) query.getOffset());
        }
        if (query.getLimit() != 0) {
            mappedQuery.limit((int) query.getLimit());
        }
        if (query.getSort() != null) {
            mappedQuery.with(query.getSort());
        }
        return mappedQuery;
    }

    private String mongoFullTextSearchErrorMessage() {
        return "### MongoDB Full Text Search does not work properly - cannot retrieve any results.";
    }

}