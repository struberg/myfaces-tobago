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

package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.component.RendererTypes;
import org.apache.myfaces.tobago.component.UITreeCommand;
import org.apache.myfaces.tobago.component.UITreeLabel;
import org.apache.myfaces.tobago.component.UITreeNode;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

public class TreeMenuRenderer extends TreeRenderer {

  @Override
  public void prepareRender(FacesContext facesContext, UIComponent component) throws IOException {
    super.prepareRender(facesContext, component);

    setRendererTypeForCommandsAndNodes(component);
  }

  protected void setRendererTypeForCommandsAndNodes(UIComponent component) {
    for (UIComponent child : (List<UIComponent>) component.getChildren()) {
      if (child instanceof UITreeNode) {
        child.setRendererType(RendererTypes.TREE_MENU_NODE);
      }
      if (child instanceof UITreeCommand) {
        child.setRendererType(RendererTypes.TREE_MENU_COMMAND);
      }
      if (child instanceof UITreeLabel) {
        child.setRendererType(RendererTypes.TREE_MENU_LABEL);
      }
      setRendererTypeForCommandsAndNodes(child);
    }
  }
}