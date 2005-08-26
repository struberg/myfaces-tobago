/*
 * Copyright 2002-2005 atanion GmbH.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/*
 * Created 22.06.2004 09:08:37.
 * $Id$
 */
package org.apache.myfaces.tobago.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.tobago.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Collections;

public class SheetState {

  private static final Log LOG = LogFactory.getLog(SheetState.class);
  public static final String SEPARATOR = ",";

  private int sortedColumn = -1;
  private boolean ascending;
  private String selected;
  private String columnWidths;

  public SheetState() {
    resetSelected();
  }

  public void resetSelected() {
    selected = "";
  }

  public List<Integer> getSelectedIndices() {
    try {
      return StringUtil.parseIntegerList(selected);
    } catch (NumberFormatException e) {
      LOG.warn(selected, e);
      return Collections.emptyList();
    }
  }

  public int getSortedColumn() {
    return sortedColumn;
  }

  public void setSortedColumn(int sortedColumn) {
    this.sortedColumn = sortedColumn;
  }

  public boolean isAscending() {
    return ascending;
  }

  public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }

  public String getSelected() {
    return selected;
  }

  public void setSelected(String selected) {
    this.selected = selected;
  }

  public String getColumnWidths() {
    return columnWidths;
  }

  public void setColumnWidths(String columnWidths) {
    this.columnWidths = columnWidths;
  }

  public String debugSorted() {
    StringBuffer sb = new StringBuffer("\nsorted column = ");
    sb.append(sortedColumn);
    sb.append("\nascending = ");
    sb.append(ascending);
    return sb.toString();
  }
}
