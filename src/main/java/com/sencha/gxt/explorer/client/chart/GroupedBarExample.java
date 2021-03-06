/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.explorer.client.chart;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.examples.resources.client.TestData;
import com.sencha.gxt.examples.resources.client.model.Data;
import com.sencha.gxt.explorer.client.model.Example.Detail;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

@Detail(name = "Grouped Bar Chart", icon = "groupedbarchart", category = "Charts", classes = Data.class)
public class GroupedBarExample implements IsWidget, EntryPoint {

  public interface DataPropertyAccess extends PropertyAccess<Data> {
    ValueProvider<Data, Double> data1();

    ValueProvider<Data, Double> data2();

    ValueProvider<Data, Double> data3();

    ValueProvider<Data, String> name();

    @Path("id")
    ModelKeyProvider<Data> nameKey();
  }

  private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

  private FramedPanel panel;

  @Override
  public Widget asWidget() {
    if (panel == null) {
      final ListStore<Data> store = new ListStore<Data>(dataAccess.nameKey());
      store.addAll(TestData.getData(12, 20, 100));

      final Chart<Data> chart = new Chart<Data>();
      chart.setStore(store);
      chart.setShadowChart(false);

      NumericAxis<Data> axis = new NumericAxis<Data>();
      axis.setPosition(Position.BOTTOM);
      axis.addField(dataAccess.data1());
      axis.addField(dataAccess.data2());
      axis.addField(dataAccess.data3());
      TextSprite title = new TextSprite("Number of Hits");
      title.setFontSize(18);
      axis.setTitleConfig(title);
      axis.setDisplayGrid(true);
      axis.setMinimum(0);
      axis.setMaximum(100);
      chart.addAxis(axis);

      CategoryAxis<Data, String> catAxis = new CategoryAxis<Data, String>();
      catAxis.setPosition(Position.LEFT);
      catAxis.setField(dataAccess.name());
      title = new TextSprite("Month of the Year");
      title.setFontSize(18);
      catAxis.setTitleConfig(title);
      chart.addAxis(catAxis);

      final BarSeries<Data> bar = new BarSeries<Data>();
      bar.setYAxisPosition(Position.BOTTOM);
      bar.addYField(dataAccess.data1());
      bar.addYField(dataAccess.data2());
      bar.addYField(dataAccess.data3());
      bar.addColor(new RGB(148, 174, 10));
      bar.addColor(new RGB(17, 95, 166));
      bar.addColor(new RGB(166, 17, 32));
      chart.addSeries(bar);

      final Legend<Data> legend = new Legend<Data>();
      legend.setItemHighlighting(true);
      legend.setItemHiding(true);
      legend.getBorderConfig().setStrokeWidth(0);
      chart.setLegend(legend);

      TextButton regenerate = new TextButton("Reload Data");
      regenerate.addSelectHandler(new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          store.clear();
          store.addAll(TestData.getData(12, 0, 100));
          chart.redrawChart();
        }
      });

      ToggleButton animation = new ToggleButton("Animate");
      animation.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
          chart.setAnimated(event.getValue());
        }
      });
      animation.setValue(true, true);
      ToggleButton shadow = new ToggleButton("Shadow");
      shadow.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
          chart.setShadowChart(event.getValue());
          chart.redrawChart();
        }
      });
      shadow.setValue(false);

      ToolBar toolBar = new ToolBar();
      toolBar.add(regenerate);
      toolBar.add(animation);
      toolBar.add(shadow);

      panel = new FramedPanel();
      panel.setLayoutData(new MarginData(10));
      panel.setCollapsible(true);
      panel.setHeadingText("Grouped Bar Chart");
      panel.setPixelSize(620, 500);
      panel.setBodyBorder(true);

      final Resizable resize = new Resizable(panel, Dir.E, Dir.SE, Dir.S);
      resize.setMinHeight(400);
      resize.setMinWidth(400);

      panel.addExpandHandler(new ExpandHandler() {
        @Override
        public void onExpand(ExpandEvent event) {
          resize.setEnabled(true);
        }
      });
      panel.addCollapseHandler(new CollapseHandler() {
        @Override
        public void onCollapse(CollapseEvent event) {
          resize.setEnabled(false);
        }
      });

      new Draggable(panel, panel.getHeader()).setUseProxy(false);

      VerticalLayoutContainer layout = new VerticalLayoutContainer();
      panel.add(layout);

      toolBar.setLayoutData(new VerticalLayoutData(1, -1));
      layout.add(toolBar);

      chart.setLayoutData(new VerticalLayoutData(1, 1));
      layout.add(chart);
    }

    return panel;
  }

  public void onModuleLoad() {
    RootPanel.get().add(asWidget());
  }
}
