package com.comsysto.movie.ui.page.home;

import com.comsysto.movie.ui.page.AbstractBasePage;
import com.comsysto.movie.ui.panel.MovieListPanel;
import org.apache.wicket.Component;

public class HomePage extends AbstractBasePage {


    public HomePage() {
        super();
        setOutputMarkupId(true);

        add(movieListPanel());
    }

    private Component movieListPanel() {
        return new MovieListPanel("movieListPanel");
    }

}
