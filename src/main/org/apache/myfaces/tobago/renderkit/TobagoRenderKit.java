/*
 * Copyright (c) 2003 Atanion GmbH, Germany
 * All rights reserved. Created 08.04.2003 10:45:30.
 * $Id$
 */
package com.atanion.tobago.renderkit;

import com.atanion.tobago.context.ResourceManager;
import com.atanion.tobago.context.ResourceManagerUtil;
import com.atanion.tobago.webapp.TobagoResponseWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.OutputStream;
import java.io.Writer;

public class TobagoRenderKit extends RenderKit {

  private static final Log LOG = LogFactory.getLog(TobagoRenderKit.class);

  public static final String RENDER_KIT_ID = "tobago";

  public static final String PACKAGE_PREFIX
      = TobagoRenderKit.class.getName().substring(
          0,
          TobagoRenderKit.class.getName().lastIndexOf('.'));

  public static final int PACKAGE_PREFIX_LENGTH = PACKAGE_PREFIX.length();

  private ResourceManager resources;

  // fixme: use family
  public Renderer getRenderer(String family, String rendererType) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("family = '" + family + "'");
    }
    Renderer renderer = null;
    if (rendererType != null) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (resources == null) {
        resources = ResourceManagerUtil.getResourceManager(facesContext);
      }
      renderer = resources.getRenderer(facesContext.getViewRoot(), rendererType);
    }
    if (renderer == null) {
      LOG.error(
          "The class witch was found by the ResourceManager can't be " +
          "found, or instanciated: classname='" + rendererType + "'");
    }
    return renderer;
  }

  public ResponseWriter createResponseWriter(
      Writer writer, String contentTypeList, String characterEncoding) {
    String contentType;
    if (contentTypeList == null) {
      contentType = "text/html";
    } else if (contentTypeList.indexOf("text/html") > -1) {
      contentType = "text/html";
      LOG.warn("patching content type from " +contentTypeList + " to " + contentType+"'");
    } else if (contentTypeList.indexOf("text/fo") > -1) {
      contentType = "text/fo";
      LOG.warn("patching content type from " +contentTypeList + " to " + contentType+"'");
    } else {
      contentType = "text/html";
      LOG.warn("Content-Type '" + contentTypeList + "' not supported!" +
          " Using text/html");
    }

    return new TobagoResponseWriter(writer, contentType, characterEncoding);
  }


// ///////////////////////////////////////////// todo

  public void addRenderer(String family, String rendererType, Renderer renderer) {
//    synchronized(renderers) {
//      renderers.put(family + SEP + rendererType, renderer);
//    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("addRenderer family='" + family
          + "' rendererType='" + rendererType + "'");
    }
    LOG.error(
        "This method isn't implemented yet, and should not be called: "
        + new Exception().getStackTrace()[0].getMethodName()); //fixme jsf1.0
  }

  public ResponseStateManager getResponseStateManager() {
    LOG.error(
        "This method isn't implemented yet, and should not be called: "
        + new Exception().getStackTrace()[0].getMethodName()); //fixme jsfbeta
    return null;
  }

  public ResponseStream createResponseStream(OutputStream outputstream) {
    LOG.error(
        "This method isn't implemented yet, and should not be called: "
        + new Exception().getStackTrace()[0].getMethodName()); //fixme jsfbeta
    return null;
  }
}
