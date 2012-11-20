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

package org.apache.myfaces.tobago.internal.component;

import org.apache.commons.collections.KeyValue;
import org.apache.myfaces.tobago.compat.FacesUtils;
import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.ComponentTypes;
import org.apache.myfaces.tobago.component.DeprecatedDimension;
import org.apache.myfaces.tobago.component.Facets;
import org.apache.myfaces.tobago.component.OnComponentPopulated;
import org.apache.myfaces.tobago.component.RendererTypes;
import org.apache.myfaces.tobago.internal.layout.LayoutUtils;
import org.apache.myfaces.tobago.internal.util.Deprecation;
import org.apache.myfaces.tobago.internal.webapp.TobagoMultipartFormdataRequest;
import org.apache.myfaces.tobago.layout.Box;
import org.apache.myfaces.tobago.layout.LayoutComponent;
import org.apache.myfaces.tobago.layout.LayoutContainer;
import org.apache.myfaces.tobago.layout.LayoutManager;
import org.apache.myfaces.tobago.layout.Measure;
import org.apache.myfaces.tobago.model.PageState;
import org.apache.myfaces.tobago.model.PageStateImpl;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.util.CreateComponentUtils;
import org.apache.myfaces.tobago.util.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractUIPage extends AbstractUIForm
    implements OnComponentPopulated, LayoutContainer, DeprecatedDimension {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractUIPage.class);

  public static final String COMPONENT_TYPE = "org.apache.myfaces.tobago.Page";

  public static final String FORM_ACCEPT_CHARSET = "utf-8";

  private String formId;

  private String actionId;

  private Box actionPosition;

  private List<KeyValue> postfields;

  @Override
  public boolean getRendersChildren() {
    return true;
  }

  public String getFormId(FacesContext facesContext) {
    if (formId == null) {
      formId = getClientId(facesContext) + ComponentUtils.SUB_SEPARATOR + "form";
    }
    return formId;
  }

  @Override
  public void encodeBegin(FacesContext facesContext) throws IOException {

    super.encodeBegin(facesContext);
    ((AbstractUILayoutBase) getLayoutManager()).encodeBegin(facesContext);
  }

  @Override
  public void encodeChildren(FacesContext facesContext) throws IOException {

    ((AbstractUILayoutBase) getLayoutManager()).encodeChildren(facesContext);
  }

  @Override
  public void encodeEnd(FacesContext facesContext) throws IOException {

    ((AbstractUILayoutBase) getLayoutManager()).encodeEnd(facesContext);
    super.encodeEnd(facesContext);
  }

  @Override
  public void processDecodes(FacesContext facesContext) {

    checkTobagoRequest(facesContext);

    decode(facesContext);

    markSubmittedForm(facesContext);

    // invoke processDecodes() on children
    for (Iterator kids = getFacetsAndChildren(); kids.hasNext();) {
      UIComponent kid = (UIComponent) kids.next();
      kid.processDecodes(facesContext);
    }
  }

  public void markSubmittedForm(FacesContext facesContext) {
    // find the form of the action command and set submitted to it and all
    // children

    // reset old submitted state
    setSubmitted(false);

    String currentActionId = getActionId();
    if (LOG.isDebugEnabled()) {
      LOG.debug("actionId = '" + currentActionId + "'");
    }

    UIComponent command = null;
    try { // todo: try bock should be removed, when tested, that the warning usually not occure.
      command = findComponent(currentActionId);
    } catch (Exception e) {
      LOG.warn("Should not happen!!! currentActionId='" + currentActionId + "'");
    }

    // TODO: remove this if block if proven this never happens anymore
    if (command == null
        && currentActionId != null && currentActionId.matches(".*:\\d+:.*")) {
      // If currentActionId component was inside a sheet the id contains the
      // rowIndex and is therefore not found here.
      // We do not need the row here because we want just to find the
      // related form, so removing the rowIndex will help here.
      currentActionId = currentActionId.replaceAll(":\\d+:", ":");
      try {
        command = findComponent(currentActionId);
        //LOG.info("command = \"" + command + "\"", new Exception());
      } catch (Exception e) {
        // ignore
      }
    }

    if (LOG.isTraceEnabled()) {
      LOG.trace(currentActionId);
      LOG.trace("command:{}", command);
      LOG.trace(DebugUtils.toString(facesContext.getViewRoot(), 0));
    }

    if (command != null) {
      AbstractUIForm form = ComponentUtils.findForm(command);
      form.setSubmitted(true);

      if (LOG.isTraceEnabled()) {
        LOG.trace("form:{}", form);
        LOG.trace(form.getClientId(facesContext));
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Illegal actionId! Rerender the view.");
      }
      facesContext.renderResponse();
    }
  }

  private void checkTobagoRequest(FacesContext facesContext) {
    // multipart/form-data must use TobagoMultipartFormdataRequest
    String contentType = (String) facesContext.getExternalContext()
        .getRequestHeaderMap().get("content-type");
    if (contentType != null && contentType.startsWith("multipart/form-data")) {
      Object request = facesContext.getExternalContext().getRequest();
      boolean okay = false;
      if (request instanceof TobagoMultipartFormdataRequest) {
        okay = true;
      } else if (request instanceof HttpServletRequestWrapper) {
        ServletRequest wrappedRequest
            = ((HttpServletRequestWrapper) request).getRequest();
        if (wrappedRequest instanceof TobagoMultipartFormdataRequest) {
          okay = true;
        }
      }
      // TODO PortletRequest ??
      if (!okay) {
        LOG.error("Can't process multipart/form-data without TobagoRequest. "
            + "Please check the web.xml and define a TobagoMultipartFormdataFilter. "
            + "See documentation for <tc:file>");
        facesContext.addMessage(null, new FacesMessage("An error has occured!"));
      }
    }
  }

  public List<KeyValue> getPostfields() {
    if (postfields == null) {
      postfields = new ArrayList<KeyValue>();
    }
    return postfields;
  }

  @Override
  public void processUpdates(FacesContext context) {
    super.processUpdates(context);
    updatePageState(context);
  }

  /**
   *
   * @deprecated PageState is deprecated since 1.5.0
   */
  @Deprecated
  public void updatePageState(FacesContext facesContext) {
  }

  /**
   *
   * @deprecated PageState is deprecated since 1.5.0
   */
  @Deprecated
  public PageState getPageState(FacesContext facesContext) {
    if (FacesUtils.hasValueBindingOrValueExpression(this, Attributes.STATE)) {
      PageState state = (PageState)
          FacesUtils.getValueFromValueBindingOrValueExpression(facesContext, this, Attributes.STATE);
      if (state == null) {
        state = new PageStateImpl();
        FacesUtils.setValueOfBindingOrExpression(facesContext, state, this, Attributes.STATE);
      }
      return state;
    } else {
      return null;
    }
  }

  public String getActionId() {
    return actionId;
  }

  public void setActionId(String actionId) {
    this.actionId = actionId;
  }

  public Box getActionPosition() {
    return actionPosition;
  }

  public void setActionPosition(Box actionPosition) {
    this.actionPosition = actionPosition;
  }

  /** @deprecated since 1.5.7 and 1.6.0 */
  public String getDefaultActionId() {
    Deprecation.LOG.error("The default action handling has been changed!");
    return null;
  }

  /** @deprecated since 1.5.7 and 1.6.0 */
  public void setDefaultActionId(String defaultActionId) {
    Deprecation.LOG.error("The default action handling has been changed!");
  }

  public void onComponentPopulated(FacesContext facesContext, UIComponent parent) {
    if (getLayoutManager() == null) {
      setLayoutManager(CreateComponentUtils.createAndInitLayout(
          facesContext, ComponentTypes.GRID_LAYOUT, RendererTypes.GRID_LAYOUT, parent));
    }
  }

  public List<LayoutComponent> getComponents() {
    return LayoutUtils.findLayoutChildren(this);
  }

  public LayoutManager getLayoutManager() {
    return (LayoutManager) getFacet(Facets.LAYOUT);
  }

  public void setLayoutManager(LayoutManager layoutManager) {
    getFacets().put(Facets.LAYOUT, (AbstractUILayoutBase) layoutManager);
  }

  public boolean isLayoutChildren() {
    return isRendered();
  }

  public abstract Measure getWidth();

  public abstract Measure getHeight();
}