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
 * All rights reserved. Created 07.02.2003 16:00:00.
 * $Id$
 */
package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.TobagoConstants;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class VerbatimRenderer extends RendererBase  {

// ///////////////////////////////////////////// constant

  private static final Log LOG = LogFactory.getLog(VerbatimRenderer.class);

// ///////////////////////////////////////////// attribute

// ///////////////////////////////////////////// constructor

// ///////////////////////////////////////////// code

  public void encodeEndTobago(FacesContext facesContext,
      UIComponent component) throws IOException {

    LOG.info("HI! " + component.getClientId(facesContext), new Exception());

    ResponseWriter writer = facesContext.getResponseWriter();

    String value = ComponentUtil.currentValue(component);
    if (value == null) {
      return;
    }

    if (ComponentUtil.getBooleanAttribute(component, TobagoConstants.ATTR_ESCAPE)) {
      writer.writeText(value, null);
    }
    else {
      writer.write(value);
    }

  }

// ///////////////////////////////////////////// bean getter + setter

}
