package com.comsysto.movie.ui.panel;

import com.comsysto.movie.repository.model.Movie;
import com.comsysto.movie.repository.query.MovieQuery;
import com.comsysto.movie.service.api.MovieService;
import com.comsysto.movie.ui.customcomponent.CheckBoxColumn;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 6/5/13
 * Time: 10:27 AM
 */
public class MovieListPanel extends Panel {

    @SpringBean
    private MovieService movieService;
    private final IModel<String> searchStringModel = Model.of("");
    private final IModel<Boolean> fullTextSearchCheckboxModel = Model.of(true);

    public MovieListPanel(String id) {
        super(id);
        add(movieListFilterForm());
        add(movieList());
        add(movieImportForm());
    }

    private Component movieImportForm() {
        final IModel<Integer> movieImportCounterModel = Model.of(500);

        Form form = new Form("movieImportForm");
        form.add(new NumberTextField<Integer>("movieImportCounter", movieImportCounterModel));
        form.add(new IndicatingAjaxButton("movieImportButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                movieService.importMovies(movieImportCounterModel.getObject(), true);
                target.add(getPage());
            }
        });

        return form;
    }

    private Component movieListFilterForm() {
        Form form = new Form("movieListFilterForm");
        form.add(new TextField("searchString", searchStringModel));
        form.add(new CheckBox("fullTextSearchCheckbox", fullTextSearchCheckboxModel));
        return form;
    }

    private Component movieList() {
        SortableDataProvider<Movie, String> dataProvider = new SortableDataProvider<Movie, String>() {
            @Override
            public Iterator<? extends Movie> iterator(long first, long count) {
                return movieService.findByQuery(createQuery(first, count)).iterator();
            }

            private MovieQuery createQuery(long first, long count) {
                MovieQuery query = MovieQuery.MovieQueryBuilder.create()
                        .withDescriptionFullTextSearch(createSearchString(!fullTextSearchCheckboxModel.getObject()))
                        .withDescriptionNoFullTextSearch(createSearchString(fullTextSearchCheckboxModel.getObject()))
                        .withSort(createSortObject())
                        .withPagination(first, count)
                        .build();
                return query;
            }

            private String createSearchString(boolean alwaysReturnNull) {
                if (alwaysReturnNull) {
                    return null;
                }
                String searchString = null;
                if (searchStringModel != null && searchStringModel.getObject() != null && !searchStringModel.getObject().isEmpty()) {
                    searchString = searchStringModel.getObject();
                }
                return searchString;
            }

            private Sort createSortObject() {
                Sort.Direction sortDirection = Sort.Direction.ASC;
                if (!getSort().isAscending()) {
                    sortDirection = Sort.Direction.DESC;
                }
                return new Sort(sortDirection, getSort().getProperty());
            }

            @Override
            public long size() {
                return movieService.countForQuery(createQuery(0, 0));
            }

            @Override
            public IModel<Movie> model(Movie object) {
                return Model.of(object);
            }

            @Override
            public void detach() {
                // nothing to do
            }
        };

        dataProvider.setSort(new SortParam<String>("title", true));

        List<IColumn<Movie, String>> columns = new ArrayList<IColumn<Movie, String>>(5);
        columns.add(new PropertyColumn<Movie, String>(new Model("Title"), "title", "title"));
        columns.add(new PropertyColumn<Movie, String>(new Model("Description"), "description", "description"));
        columns.add(new PropertyColumn<Movie, String>(new Model("Year"), "year", "year"));
        columns.add(new CheckBoxColumn<Movie, String>(new Model("AW"), "alreadyWatched") {
            @Override
            protected IModel<Boolean> newCheckBoxModel(IModel<Movie> rowModel) {
                return Model.of(rowModel.getObject().isAlreadyWatched());
            }
        });
        columns.add(new CheckBoxColumn<Movie, String>(new Model("LW"), "likeToWatch") {
            @Override
            protected IModel<Boolean> newCheckBoxModel(IModel<Movie> rowModel) {
                return Model.of(rowModel.getObject().isLikeToWatch());
            }
        });

        DataTable<Movie, String> dataTable = new DefaultDataTable<Movie, String>("movieList", columns, dataProvider, 15);
        return dataTable;
    }
}