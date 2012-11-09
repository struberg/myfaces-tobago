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

package org.apache.myfaces.tobago.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/*
 * Date: Nov 12, 2006
 * Time: 10:32:39 AM
 */
public class DebugNavigationHandler extends NavigationHandler {

  private static final Log LOG = LogFactory.getLog(DebugNavigationHandler.class);

  private NavigationHandler navigationHandler;

  public DebugNavigationHandler(NavigationHandler navigationHandler) {
    this.navigationHandler = navigationHandler;
  }

  public void handleNavigation(FacesContext context, String fromAction, String outcome) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Handle Navigation context: " + context + " fromAction: '"
          + fromAction + "' outcome: '" + outcome + "'");
    }
    navigationHandler.handleNavigation(context, fromAction, outcome);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Handled Navigation context: " + context + " fromAction: '"
          + fromAction + "' outcome: '" + outcome + "'");
    }

  }
}