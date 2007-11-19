package com.intellij.debugger.ui.content.newUI;

import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class GridCell {

  private Grid myContainer;

  private List<Content> myContents = new ArrayList<Content>();
  private JBTabs myTabs;
  private Grid.Placeholder myPlaceholder;
  private PlaceInGrid myPlaceInGrid;

  public GridCell(Project project, Grid container, Grid.Placeholder placeholder, boolean horizontalToolbars, PlaceInGrid placeInGrid) {
    myContainer = container;
    myPlaceInGrid = placeInGrid;
    myPlaceholder = placeholder;
    myTabs = new JBTabs(project, container.myActionManager, container);
    myTabs.setUiDecorator(new JBTabs.UiDecorator() {
      public JBTabs.UiDecoration getDecoration() {
        return new JBTabs.UiDecoration(null, new Insets(0, -1, 0, -1));
      }
    });
    myTabs.setSideComponentVertical(!horizontalToolbars);
    myTabs.setStealthTabMode(true);
  }

  public PlaceInGrid getPlaceInGrid() {
    return myPlaceInGrid;
  }

  void add(Content content) {
    if (myContents.contains(content)) return;
    myContents.add(content);

    revalidateCell();
  }

  void remove(Content content) {
    if (!myContents.contains(content)) return;
    myContents.remove(content);

    revalidateCell();
  }

  private void revalidateCell() {

    if (myContents.size() == 0) {
      myPlaceholder.removeAll();
    } else {
      if (myPlaceholder.isNull()) {
        myPlaceholder.setContent(myTabs);
      }

      myTabs.removeAllTabs();
      for (Content each : myContents) {
        myTabs.addTab(getTabInfoFor(each));
      }
    }

    restoreProportion();

    myTabs.revalidate();
    myTabs.repaint();
  }

  void setHideTabs(boolean hide) {
    myTabs.setHideTabs(hide);
  }

  private TabInfo getTabInfoFor(Content content) {
    final JComponent c = content.getComponent();

    NewDebuggerContentUI.removeScrollBorder(c);

    return new TabInfo(c)
      .setIcon(content.getIcon())
      .setText(content.getDisplayName())
      .setActions(content.getActions(), content.getPlace())
      .setObject(content)
      .setPreferredFocusableComponent(content.getPreferredFocusableComponent());
  }

  public void setToolbarHorizontal(final boolean horizontal) {
    myTabs.setSideComponentVertical(!horizontal);
  }

  public void restoreProportion() {
    myContainer.restoreProportion(myPlaceInGrid);
  }
}
