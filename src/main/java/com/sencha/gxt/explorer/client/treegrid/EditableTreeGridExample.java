/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.explorer.client.treegrid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.examples.resources.client.TestData;
import com.sencha.gxt.examples.resources.client.images.ExampleImages;
import com.sencha.gxt.examples.resources.client.model.BaseDto;
import com.sencha.gxt.examples.resources.client.model.FolderDto;
import com.sencha.gxt.examples.resources.client.model.MusicDto;
import com.sencha.gxt.explorer.client.model.Example;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

@Example.Detail(name = "Editable TreeGrid", category = "TreeGrid", icon = "editortreegrid")
public class EditableTreeGridExample implements IsWidget, EntryPoint {

  class KeyProvider implements ModelKeyProvider<BaseDto> {
    @Override
    public String getKey(BaseDto item) {
      return (item instanceof FolderDto ? "f-" : "m-") + item.getId().toString();
    }
  }

  private FramedPanel panel;

  @Override
  public Widget asWidget() {
    if (panel == null) {
      panel = new FramedPanel();
      panel.setHeadingText("TreeGrid Editing");
      panel.addStyleName("margin-10");
      panel.setPixelSize(600, 300);

      VerticalLayoutContainer v = new VerticalLayoutContainer();
      v.setBorders(true);
      panel.add(v);

      TreeStore<BaseDto> store = new TreeStore<BaseDto>(new KeyProvider());

      FolderDto root = TestData.getMusicRootFolder();
      for (BaseDto base : root.getChildren()) {
        store.add(base);
        if (base instanceof FolderDto) {
          processFolder(store, (FolderDto) base);
        }
      }

      ColumnConfig<BaseDto, String> cc1 = new ColumnConfig<BaseDto, String>(new ValueProvider<BaseDto, String>() {

        @Override
        public String getPath() {
          return "name";
        }

        @Override
        public String getValue(BaseDto object) {
          return object.getName();
        }

        @Override
        public void setValue(BaseDto object, String value) {
          object.setName(value);
        }
      });
      cc1.setHeader(SafeHtmlUtils.fromString("Name"));

      ColumnConfig<BaseDto, String> cc2 = new ColumnConfig<BaseDto, String>(new ValueProvider<BaseDto, String>() {

        @Override
        public String getPath() {
          return "author";
        }

        @Override
        public String getValue(BaseDto object) {
          return object instanceof MusicDto ? ((MusicDto) object).getAuthor() : null;
        }

        @Override
        public void setValue(BaseDto object, String value) {
          if (object instanceof MusicDto) {
            ((MusicDto) object).setAuthor(value);
          }
        }
      });
      cc2.setHeader(SafeHtmlUtils.fromString("Author"));

      ColumnConfig<BaseDto, String> cc3 = new ColumnConfig<BaseDto, String>(new ValueProvider<BaseDto, String>() {

        @Override
        public String getPath() {
          return "genre";
        }

        @Override
        public String getValue(BaseDto object) {
          return object instanceof MusicDto ? ((MusicDto) object).getGenre() : null;
        }

        @Override
        public void setValue(BaseDto object, String value) {
          if (object instanceof MusicDto) {
            ((MusicDto) object).setGenre(value);
          }
        }
      });
      cc3.setHeader("Genre");
      cc3.setCell(new TextCell());
      List<ColumnConfig<BaseDto, ?>> l = new ArrayList<ColumnConfig<BaseDto, ?>>();
      l.add(cc1);
      l.add(cc2);
      l.add(cc3);
      ColumnModel<BaseDto> cm = new ColumnModel<BaseDto>(l);

      final TreeGrid<BaseDto> tree = new TreeGrid<BaseDto>(store, cm, cc1);
      tree.setExpandOnDoubleClick(false);
      tree.setSelectionModel(new CellSelectionModel<BaseDto>());
      tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());
      tree.getView().setAutoExpandColumn(cc1);

      // EDITING//
      final GridInlineEditing<BaseDto> editing = new GridInlineEditing<BaseDto>(tree);
      editing.setClicksToEdit(ClicksToEdit.TWO);
      editing.addEditor(cc1, new TextField());
      editing.addEditor(cc2, new TextField());
      editing.addEditor(cc3, new TextField());
      // EDITING//

      ToolBar buttonBar = new ToolBar();

      buttonBar.add(new TextButton("Expand All", new SelectHandler() {

        @Override
        public void onSelect(SelectEvent event) {
          tree.expandAll();
        }
      }));
      buttonBar.add(new TextButton("Collapse All", new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          tree.collapseAll();
        }

      }));

      SimpleComboBox<ClicksToEdit> clicksToEdit = new SimpleComboBox<ClicksToEdit>(new LabelProvider<ClicksToEdit>() {

        @Override
        public String getLabel(ClicksToEdit item) {
          return item == ClicksToEdit.ONE ? "1" : "2";
        }
      });
      clicksToEdit.setWidth(50);
      clicksToEdit.setEditable(false);
      clicksToEdit.setForceSelection(true);
      clicksToEdit.setTriggerAction(TriggerAction.ALL);
      clicksToEdit.addValueChangeHandler(new ValueChangeHandler<ClicksToEdit>() {
        @Override
        public void onValueChange(ValueChangeEvent<ClicksToEdit> event) {
          editing.setClicksToEdit(event.getValue());
          tree.setExpandOnDoubleClick(event.getValue() == ClicksToEdit.ONE);
        }
      });

      clicksToEdit.add(ClicksToEdit.ONE);
      clicksToEdit.add(ClicksToEdit.TWO);
      clicksToEdit.setValue(ClicksToEdit.TWO);

      buttonBar.add(new SeparatorToolItem());
      buttonBar.add(new LabelToolItem("Click to edit:"));
      buttonBar.add(clicksToEdit);

      v.add(buttonBar, new VerticalLayoutData(1, -1));
      v.add(tree, new VerticalLayoutData(1, 1));

    }
    return panel;
  }

  public void onModuleLoad() {
    RootPanel.get().add(this);
  }

  private void processFolder(TreeStore<BaseDto> store, FolderDto folder) {
    for (BaseDto child : folder.getChildren()) {
      store.add(folder, child);
      if (child instanceof FolderDto) {
        processFolder(store, (FolderDto) child);
      }
    }
  }
}
