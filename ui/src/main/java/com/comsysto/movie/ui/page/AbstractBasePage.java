package com.comsysto.movie.ui.page;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

import java.awt.*;
import java.util.Collections;
import java.util.Map;


public abstract class AbstractBasePage extends WebPage {

    private static final String FAVICON_HEADER = "<link rel=\"icon\" type=\"image/png\" href=\"${contextPath}/assetsBuildFromCommon/ico/Icons_Map-14.png\" />";

    protected WebMarkupContainer header;
    protected WebMarkupContainer navigation;
    protected Component feedback;

    public AbstractBasePage(PageParameters pageParameters) {
        super(pageParameters);
        initialize();
    }

    public AbstractBasePage(IModel<?> model) {
        super(model);
        initialize();
    }

    public AbstractBasePage() {
        super();
        initialize();
    }

    protected String getContextPath() {
        return RequestCycle.get().getRequest().getContextPath();
    }

    private void initialize() {

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        String contextPath = RequestCycle.get().getRequest().getContextPath();

        Map<String, String> replacements = Collections.singletonMap("contextPath", getContextPath());
        MapVariableInterpolator variableInterpolator = new MapVariableInterpolator(FAVICON_HEADER, replacements);
        response.render(StringHeaderItem.forString(variableInterpolator.toString()));

        response.render(CssHeaderItem.forUrl(contextPath + "/css/bootstrap.css"));
        response.render(CssHeaderItem.forUrl(contextPath + "/css/bootstrap-responsive.css"));

        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-modal.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-dropdown.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-scrollspy.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-tab.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-tooltip.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-popover.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-alert.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-button.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-carousel.js"));
        response.render(JavaScriptHeaderItem.forUrl(contextPath + "/js/bootstrap-typeahead.js"));


    }

}