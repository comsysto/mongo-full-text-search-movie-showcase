package com.comsysto.movie.ui.customcomponent;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 6/5/13
 * Time: 4:49 PM
 */
public abstract class CheckBoxColumn<T, S> extends AbstractColumn<T, S>
{
    public CheckBoxColumn(IModel<String> displayModel, S sortProperty) {
        super(displayModel, sortProperty);
    }

    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new CheckPanel(componentId, newCheckBoxModel(rowModel)));
    }

    protected CheckBox newCheckBox(String id, IModel<Boolean> checkModel) {
        return new CheckBox("check", checkModel);
    }

    protected abstract IModel<Boolean> newCheckBoxModel(IModel<T> rowModel);

    private class CheckPanel extends Panel {
        public CheckPanel(String id, IModel<Boolean> checkModel) {
            super(id);
            add(newCheckBox("check", checkModel));
        }
    }
}
