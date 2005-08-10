/*
 * Copyright (c) 2003 Atanion GmbH, Germany
 * All rights reserved. Created 07.02.2003 16:00:00.
 * $Id$
 */
package com.atanion.tobago.renderkit.html.scarborough.standard.tag;

import com.atanion.tobago.TobagoConstants;
import com.atanion.tobago.component.ComponentUtil;
import com.atanion.tobago.renderkit.RendererBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class TabRenderer extends RendererBase {

  private static final Log LOG = LogFactory.getLog(TabRenderer.class);

  public int getFixedHeight(FacesContext facesContext, UIComponent component) {

    int height =
        ComponentUtil.getIntAttribute(component, TobagoConstants.ATTR_HEIGHT, -1);

    if (height == -1) {
      height = PanelRenderer.getFixedHeightForPanel(component, facesContext);
      height += getConfiguredValue(facesContext, component, "paddingHeight");
    }
    return height;
  }

}
