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

package org.apache.myfaces.tobago.internal.renderkit.renderer;

import org.apache.myfaces.tobago.internal.component.AbstractUISelectBooleanCheckbox;
import org.apache.myfaces.tobago.internal.util.AccessKeyLogger;
import org.apache.myfaces.tobago.internal.util.HtmlRendererUtils;
import org.apache.myfaces.tobago.internal.util.JsonUtils;
import org.apache.myfaces.tobago.internal.util.RenderUtils;
import org.apache.myfaces.tobago.renderkit.LabelWithAccessKey;
import org.apache.myfaces.tobago.renderkit.css.BootstrapClass;
import org.apache.myfaces.tobago.renderkit.css.CssItem;
import org.apache.myfaces.tobago.renderkit.css.TobagoClass;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.HtmlInputTypes;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectBooleanCheckboxRenderer extends MessageLayoutRendererBase {

  private static final Logger LOG = LoggerFactory.getLogger(SelectBooleanCheckboxRenderer.class);

  @Override
  public void decode(final FacesContext facesContext, final UIComponent component) {

    final UIInput input = (UIInput) component;

    if (ComponentUtils.isOutputOnly(input)) {
      return;
    }

    final String newValue = facesContext.getExternalContext()
        .getRequestParameterMap().get(input.getClientId(facesContext));

    if (LOG.isDebugEnabled()) {
      LOG.debug("new value = '" + newValue + "'");
    }

    input.setSubmittedValue("true".equals(newValue) ? "true" : "false");

    RenderUtils.decodeClientBehaviors(facesContext, input);
  }

  @Override
  protected void encodeBeginField(FacesContext facesContext, UIComponent component) throws IOException {
    final AbstractUISelectBooleanCheckbox select = (AbstractUISelectBooleanCheckbox) component;
    final TobagoResponseWriter writer = getResponseWriter(facesContext);

    final String clientId = select.getClientId(facesContext);
    final String fieldId = select.getFieldId(facesContext);
    final String currentValue = getCurrentValue(facesContext, select);
    final boolean checked = "true".equals(currentValue);
    final String title = HtmlRendererUtils.getTitleFromTipAndMessages(facesContext, select);
    final boolean disabled = select.isDisabled();
    final LabelWithAccessKey label = new LabelWithAccessKey(select, true);
    final String itemLabel = select.getItemLabel();

    writer.startElement(HtmlElements.DIV);
    if (renderClientId()) {
      writer.writeIdAttribute(clientId);
    }
    writer.writeStyleAttribute(select.getStyle());

    final List<CssItem> outerCssItems = new ArrayList<CssItem>();
    addOuterCssItems(facesContext, select, outerCssItems);

    // TODO: optimize class attribute writing
    final List<CssItem> classAttributes = new ArrayList<CssItem>();
    classAttributes.add(TobagoClass.SELECT_BOOLEAN_CHECKBOX);
    classAttributes.addAll(Arrays.asList(
        TobagoClass.SELECT_BOOLEAN_CHECKBOX.createMarkup(ComponentUtils.updateMarkup(select, select.getMarkup()))));
    classAttributes.add(disabled ? BootstrapClass.DISABLED : null);
    classAttributes.add(select.getCustomClass());
    classAttributes.addAll(outerCssItems);
    writer.writeClassAttribute(null, null, classAttributes.toArray(new CssItem[classAttributes.size()]));

    HtmlRendererUtils.writeDataAttributes(facesContext, writer, select);
    if (title != null) {
      writer.writeAttribute(HtmlAttributes.TITLE, title, true);
    }

    writer.startElement(HtmlElements.LABEL);
    final List<CssItem> cssItems = new ArrayList<CssItem>();
    addCssItems(facesContext, select, cssItems);
    writer.writeClassAttribute(BootstrapClass.FORM_CHECK_LABEL,
        null,
        cssItems.toArray(new CssItem[cssItems.size()]));
    if (!disabled && label.getAccessKey() != null) {
      writer.writeAttribute(HtmlAttributes.ACCESSKEY, Character.toString(label.getAccessKey()), false);
      AccessKeyLogger.addAccessKey(facesContext, label.getAccessKey(), clientId);
    }

    writer.startElement(HtmlElements.INPUT);
    writer.writeAttribute(HtmlAttributes.TYPE, HtmlInputTypes.CHECKBOX);
    writer.writeAttribute(HtmlAttributes.VALUE, "true", false);
    writer.writeNameAttribute(clientId);
    writer.writeIdAttribute(fieldId);
    writer.writeAttribute(HtmlAttributes.CHECKED, checked);
    writer.writeAttribute(HtmlAttributes.READONLY, select.isReadonly());
    writer.writeAttribute(HtmlAttributes.DISABLED, disabled);
    writer.writeAttribute(HtmlAttributes.REQUIRED, select.isRequired());
    HtmlRendererUtils.renderFocus(clientId, select.isFocus(), ComponentUtils.isError(select), facesContext, writer);
    writer.writeAttribute(HtmlAttributes.TABINDEX, select.getTabIndex());
    writer.writeCommandMapAttribute(JsonUtils.encode(RenderUtils.getBehaviorCommands(facesContext, select)));
    writer.endElement(HtmlElements.INPUT);

    if (itemLabel != null) {
      writer.writeText(itemLabel);
    }
  }

  @Override
  protected void encodeEndField(FacesContext facesContext, UIComponent component) throws IOException {
    final TobagoResponseWriter writer = getResponseWriter(facesContext);
    writer.endElement(HtmlElements.LABEL);
    writer.endElement(HtmlElements.DIV);
  }

  protected boolean renderClientId() {
    return false;
  }

  protected void addOuterCssItems(final FacesContext facesContext, final AbstractUISelectBooleanCheckbox select,
                                  final List<CssItem> collected) {
    collected.add(BootstrapClass.FORM_CHECK);
  }

  protected void addCssItems(final FacesContext facesContext, final AbstractUISelectBooleanCheckbox select,
                             final List<CssItem> collected) {
  }
}
