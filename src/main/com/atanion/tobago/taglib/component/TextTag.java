/*
 * Copyright (c) 2001 Atanion GmbH, Germany
 * All rights reserved.
 * Created on: 15.02.2002, 17:01:56
 * $Id$
 */
package com.atanion.tobago.taglib.component;

import com.atanion.tobago.TobagoConstants;

import javax.faces.component.UIOutput;
import javax.faces.component.UIComponent;

public class TextTag extends BeanTag {

// ///////////////////////////////////////////// constant

// ///////////////////////////////////////////// attribute

  private boolean escape = true;
  private boolean verbatim = false;

// ///////////////////////////////////////////// constructor

// ///////////////////////////////////////////// code

  public String getComponentType() {
    return UIOutput.COMPONENT_TYPE;
  }

  protected void setProperties(UIComponent component) {
    super.setProperties(component);
    setProperty(component, TobagoConstants.ATTR_ESCAPE, escape);
    setProperty(component, TobagoConstants.ATTR_VERBATIM, verbatim);
  }

  public void release() {
    super.release();
    escape = true;
    verbatim = false;
  }

// ///////////////////////////////////////////// bean getter + setter

  public void setEscape(boolean escape) {
    this.escape = escape;
  }

  public void setVerbatim(boolean verbatim) {
    this.verbatim = verbatim;
  }
}
