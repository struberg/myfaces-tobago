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

package org.apache.myfaces.tobago.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.tobago.config.ThemeConfig;
import org.apache.myfaces.tobago.config.TobagoConfig;
import org.apache.myfaces.tobago.config.TobagoConfigParser;
import org.apache.myfaces.tobago.context.ResourceManagerFactory;
import org.apache.myfaces.tobago.util.LayoutUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TobagoServletContextListener implements ServletContextListener {

  private static final Log LOG
      = LogFactory.getLog(TobagoServletContextListener.class);

  public void contextInitialized(ServletContextEvent event) {

    ServletContext servletContext = event.getServletContext();

    if (servletContext.getAttribute(TobagoConfig.TOBAGO_CONFIG) != null) {
      LOG.warn("Tobago has been already initialized. Do nothing. "
          + "(This may happen when there is a TobagoServletContextListener configured manually.)");
      return;
    }

    if (LOG.isInfoEnabled()) {
      LOG.info("*** contextInitialized ***");
    }

    try {

      // tobago-config.xml
      TobagoConfig tobagoConfig
          = new TobagoConfigParser().parse(servletContext);
      servletContext.setAttribute(TobagoConfig.TOBAGO_CONFIG, tobagoConfig);

      // todo: cleanup, use one central TobagoConfig, no singleton ResourceManager
      // resources
      ResourceManagerFactory.init(servletContext, tobagoConfig);

      // apply bugfix
      LayoutUtil.setFixLayoutTransparency(tobagoConfig.isFixLayoutTransparency());
      
      // prepare themes
      tobagoConfig.resolveThemes();

      // theme config cache
      ThemeConfig.init(servletContext);

    } catch (Throwable e) {
      if (LOG.isFatalEnabled()) {
        String error = "Error while deploy process. Tobago can't be initialized! "
            + "Application will not run!";
        LOG.fatal(error, e);
        throw new RuntimeException(error, e);
      }
    }
  }

  public void contextDestroyed(ServletContextEvent event) {

    ServletContext servletContext = event.getServletContext();

    if (servletContext.getAttribute(TobagoConfig.TOBAGO_CONFIG) == null) {
      LOG.warn("Tobago is not initialized. Do nothing. "
          + "(This may happen when there is a TobagoServletContextListener configured manually.)");
      return;
    }

    if (LOG.isInfoEnabled()) {
      LOG.info("*** contextDestroyed ***\n"
          + "--- snip -----------------------------------------------------------------------");
    }

    servletContext.removeAttribute(TobagoConfig.TOBAGO_CONFIG);
    ResourceManagerFactory.release(servletContext);

    // theme config cache
    ThemeConfig.shutdown(servletContext);

    LogFactory.releaseAll();
//    LogManager.shutdown();
  }

}