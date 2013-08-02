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

package org.apache.myfaces.tobago.facelets.extension;

import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.Facets;
import org.apache.myfaces.tobago.component.InputSuggest;
import org.apache.myfaces.tobago.component.OnComponentCreated;
import org.apache.myfaces.tobago.component.OnComponentPopulated;
import org.apache.myfaces.tobago.component.RendererTypes;
import org.apache.myfaces.tobago.component.SupportsMarkup;
import org.apache.myfaces.tobago.component.UIGridLayout;
import org.apache.myfaces.tobago.component.UILabel;
import org.apache.myfaces.tobago.component.UIPanel;
import org.apache.myfaces.tobago.context.Markup;
import org.apache.myfaces.tobago.facelets.SuggestMethodRule;
import org.apache.myfaces.tobago.facelets.SupportsMarkupRule;
import org.apache.myfaces.tobago.facelets.TobagoComponentHandler;
import org.apache.myfaces.tobago.internal.layout.LayoutUtils;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.TagAttribute;
import java.io.IOException;

public abstract class TobagoLabelExtensionHandler extends ComponentHandler {
  private static final Logger LOG = LoggerFactory.getLogger(TobagoLabelExtensionHandler.class);
  private static final String DEFAULT_COLUMNS = "auto;*";
  private TagAttribute labelWidthAttribute;
  private TagAttribute tipAttribute;
  private TagAttribute labelAttribute;
  private TagAttribute markupAttribute;
  private TagAttribute fieldIdAttribute;
  private Class subComponentLastType = Object.class;
  private Metadata subComponentMapper;

  public TobagoLabelExtensionHandler(ComponentConfig config) {
    super(config);
    labelWidthAttribute = getAttribute("labelWidth");
    tipAttribute = getAttribute(Attributes.TIP);
    labelAttribute = getAttribute(Attributes.LABEL);
    markupAttribute = getAttribute(Attributes.MARKUP);
    fieldIdAttribute = getAttribute("fieldId");
  }

  protected abstract String getSubComponentType();

  protected abstract String getSubRendererType();

  protected String getRows() {
    return "auto";
  }

  protected String getColumns(String first) {
    return first + ";*";
  }

  public void applyNextHandler(FaceletContext ctx, UIComponent panel)
      throws IOException, FacesException, ELException {
    if (ComponentHandler.isNew(panel)) {
      // ensure that input has no parent (isNew)
      UIComponent input  = panel.getChildren().remove(1);
      try {
        input.getAttributes().put("tobago.panel", panel);
        nextHandler.apply(ctx, input);
      } finally {
        input.getAttributes().remove("tobago.panel");
      }
      UIComponent date = null;
      if (panel.getChildCount() > 1) {
        date = panel.getChildren().get(1);
      }
      panel.getChildren().add(input);
      if (date != null) {
        panel.getChildren().add(date);
      }
    } else {
      UIComponent input = panel.getChildren().get(1);
      nextHandler.apply(ctx, input);
    }
  }

  public void onComponentCreated(FaceletContext faceletContext, UIComponent panel, UIComponent parent) {

    Application application = faceletContext.getFacesContext().getApplication();
    UIViewRoot root = ComponentUtils.findViewRoot(faceletContext, parent);

    addGridLayout(faceletContext, panel, root);

    addLabel(faceletContext, (UIPanel) panel, root);
    String uid;
    if (fieldIdAttribute !=  null) {
      uid = fieldIdAttribute.getValue(faceletContext);
    } else {
      uid = root.createUniqueId();
    }
    if (checkForAlreadyCreated(panel, uid)) {
      return;
    }

    UIComponent input = application.createComponent(getSubComponentType());
    input.setRendererType(getSubRendererType());
    input.setId(uid);

    setSubComponentAttributes(faceletContext, input);
    enrichInput(faceletContext, input);

    panel.getChildren().add(input);
  }

  protected void enrichInput(FaceletContext faceletContext, UIComponent input) {
  }

  private void addLabel(FaceletContext faceletContext, UIPanel panel, UIViewRoot root) {
    String uid = root.createUniqueId();
    if (checkForAlreadyCreated(panel, uid)) {
      return;
    }
    Application application = faceletContext.getFacesContext().getApplication();
    UILabel label = (UILabel) application.createComponent(UILabel.COMPONENT_TYPE);
    label.setRendererType(RendererTypes.LABEL);
    label.setId(uid);
    label.getAttributes().put(Attributes.FOR, "@auto");
    if (tipAttribute != null) {
      if (tipAttribute.isLiteral()) {
        panel.setTip(tipAttribute.getValue(faceletContext));
      } else {
        ValueExpression expression = tipAttribute.getValueExpression(faceletContext, String.class);
        panel.setValueExpression(Attributes.TIP, expression);
      }
    }
    if (labelAttribute != null) {
      if (labelAttribute.isLiteral()) {
        label.setValue(labelAttribute.getValue(faceletContext));
      } else {
        ValueExpression expression = labelAttribute.getValueExpression(faceletContext, String.class);
        label.setValueExpression(Attributes.VALUE, expression);
      }
    }
    if (markupAttribute != null) {
      if (markupAttribute.isLiteral()) {
        label.setMarkup(Markup.valueOf(markupAttribute.getValue()));
      } else {
        ValueExpression expression = markupAttribute.getValueExpression(faceletContext, Object.class);
        label.setValueExpression(Attributes.MARKUP, expression);
      }
    }
    panel.getChildren().add(label);
  }

  private boolean checkForAlreadyCreated(UIComponent panel, String uid) {
    if (panel.getChildCount() > 0) {
      for (UIComponent child : panel.getChildren()) {
        if (uid.equals(child.getId())) {
          return true;
        }
      }
    }
    return false;
  }

  public void onComponentPopulated(FaceletContext faceletContext, UIComponent component, UIComponent parent) {
    super.onComponentPopulated(faceletContext, component, parent);

    if (component.getChildren().size() > 1) {
      UIComponent input = component.getChildren().get(1);
      if (input instanceof EditableValueHolder) {
        TobagoComponentHandler.addDefaultValidators(faceletContext.getFacesContext(), (EditableValueHolder) input);
      }
      if (input instanceof OnComponentPopulated) {
        ((OnComponentPopulated) input).onComponentPopulated(faceletContext.getFacesContext(), component);
      }
    }
  }

  private void addGridLayout(FaceletContext faceletContext, UIComponent panel, UIViewRoot root) {
    Application application = faceletContext.getFacesContext().getApplication();
    UIGridLayout gridLayout = (UIGridLayout) application.createComponent(UIGridLayout.COMPONENT_TYPE);
    gridLayout.setRendererType(RendererTypes.GRID_LAYOUT);
    if (labelWidthAttribute != null) {
      String columns = getColumns(labelWidthAttribute.getValue(faceletContext));
      if (!LayoutUtils.checkTokens(columns)) {
        LOG.warn("Illegal value for columns = \"" + columns + "\" replacing with default: \"" + DEFAULT_COLUMNS + "\"");
        columns = DEFAULT_COLUMNS;
      }
      gridLayout.setColumns(columns);
    } else {
      gridLayout.setColumns(getColumns("auto"));
    }
    gridLayout.setRows(getRows());
    gridLayout.setId(root.createUniqueId());
    if (gridLayout instanceof OnComponentCreated) {
      ((OnComponentCreated) gridLayout).onComponentCreated(faceletContext.getFacesContext(), panel);
    }
    panel.getFacets().put(Facets.LAYOUT, gridLayout);
    if (gridLayout instanceof OnComponentPopulated) {
      ((OnComponentPopulated) gridLayout).onComponentPopulated(faceletContext.getFacesContext(), panel);
    }
  }

  private void setSubComponentAttributes(FaceletContext ctx, Object instance) {
    if (instance != null) {
      Class type = instance.getClass();
      if (subComponentMapper == null || !subComponentLastType.equals(type)) {
        subComponentLastType = type;
        subComponentMapper = createSubComponentMetaRuleset(type).finish();
      }
      subComponentMapper.applyMetadata(ctx, instance);
    }
  }

  protected MetaRuleset createSubComponentMetaRuleset(Class aClass) {
    MetaRuleset metaRuleset = super.createMetaRuleset(aClass);
    //metaRuleset.ignore(Attributes.LABEL);
    metaRuleset.ignore(Attributes.TIP);
    metaRuleset.ignore("labelWidth");
    if (SupportsMarkup.class.isAssignableFrom(aClass)) {
      metaRuleset.addRule(SupportsMarkupRule.INSTANCE);
    }
    if (InputSuggest.class.isAssignableFrom(aClass)) {
      metaRuleset.addRule(SuggestMethodRule.INSTANCE);
    }
    return metaRuleset;
  }

  protected MetaRuleset createMetaRuleset(Class aClass) {
    MetaRuleset metaRuleset = super.createMetaRuleset(aClass);
    TagAttribute [] attrs = tag.getAttributes().getAll();
    for (int i = 0; i < attrs.length; i++) {
      TagAttribute attr = attrs[i];
      if (!attr.getLocalName().equals("rendered")) {
        metaRuleset.ignore(attr.getLocalName());
      }
    }
    return metaRuleset;
  }
}