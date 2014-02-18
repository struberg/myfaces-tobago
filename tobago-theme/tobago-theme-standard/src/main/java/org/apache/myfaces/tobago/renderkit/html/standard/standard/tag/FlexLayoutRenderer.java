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

package org.apache.myfaces.tobago.renderkit.html.standard.standard.tag;

import org.apache.myfaces.tobago.internal.component.AbstractUIFlexLayout;
import org.apache.myfaces.tobago.layout.LayoutContainer;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.renderkit.css.Classes;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtils;
import org.apache.myfaces.tobago.renderkit.util.RenderUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class FlexLayoutRenderer extends RendererBase {

  private static final Logger LOG = LoggerFactory.getLogger(FlexLayoutRenderer.class);

  @Override
  public void encodeBegin(final FacesContext facesContext, final UIComponent component) throws IOException {
    final AbstractUIFlexLayout flexLayout = (AbstractUIFlexLayout) component;
    final TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);

    writer.startElement(HtmlElements.DIV, flexLayout);
    writer.writeClassAttribute(Classes.create(flexLayout));
    // todo: const, utils, etc.
    writer.writeAttribute("data-tobago-layout", "{\"columns\":[\"auto\",1]}", true);
  }

  @Override
  public void encodeChildren(final FacesContext facesContext, final UIComponent component) throws IOException {
    final UIComponent container = component.getParent();
    if (container instanceof LayoutContainer && !((LayoutContainer) container).isLayoutChildren()) {
      return;
    }
    RenderUtils.encodeChildren(facesContext, container);
/*
    final TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);
    final AbstractUIBootstrapLayout bootstrapLayout = (AbstractUIBootstrapLayout) component;
    final UIComponent container = bootstrapLayout.getParent();
    if (container instanceof LayoutContainer && !((LayoutContainer) container).isLayoutChildren()) {
      return;
    }

    final List<UIComponent> children = container.getChildren();
    for (UIComponent child : children) {
      writer.startElement(HtmlElements.DIV, null);
      writer.writeClassAttribute("col-md-4");
      RenderUtils.encode(facesContext, child);
      writer.endElement(HtmlElements.DIV);
    }
*/
  }

  @Override
  public void encodeEnd(final FacesContext facesContext, final UIComponent component) throws IOException {
    final TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);
    writer.endElement(HtmlElements.DIV);
  }

}