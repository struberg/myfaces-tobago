/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.tobago.example.reference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.myfaces.tobago.component.UIData;

import javax.faces.event.ActionEvent;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.ArrayList;

public class PopupReferenceController {

  private static final Log LOG = LogFactory.getLog(PopupReferenceController.class);

  private Entry entry;

  private List<Entry> sheet;


  public PopupReferenceController() {
    sheet = new ArrayList<Entry>();
    for (int i = 0; i < 10; i++) {
      Entry tmp = new Entry();
      tmp.setColumn1("cell__1__" + i);
      tmp.setColumn2("cell_2_" + i);
      tmp.setColumn3("cell_3_" + i);
      sheet.add(tmp);
    }
  }

  public void selectEntry(ActionEvent event) {
    UIComponent component = event.getComponent();
    while (!(component instanceof UIData)) {
      component = component.getParent();
    }

    UIData sheet = (UIData) component;
    entry = (Entry) sheet.getRowData();
    LOG.info("entry = \"" + entry.getColumn1() + "\"");
  }

  public void saveChanges(ActionEvent event) {
    LOG.info("saveChanges()");
    // nothing to do here
  }


  public List<Entry> getSheet() {
    return sheet;
  }

  public Entry getEntry() {
    return entry;
  }

  public void setEntry(Entry entry) {
    this.entry = entry;
  }

  public static class Entry{
    private String column1;
    private String column2;
    private String column3;

    public String getColumn1() {
      return column1;
    }

    public void setColumn1(String column1) {
      this.column1 = column1;
    }

    public String getColumn2() {
      return column2;
    }

    public void setColumn2(String column2) {
      this.column2 = column2;
    }

    public String getColumn3() {
      return column3;
    }

    public void setColumn3(String column3) {
      this.column3 = column3;
    }
  }
}