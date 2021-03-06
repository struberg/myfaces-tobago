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

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public abstract class AbstractUIMeta extends UIComponentBase {

  @Override
  public void processEvent(final ComponentSystemEvent event) {
    final FacesContext facesContext = getFacesContext();
    final UIViewRoot root = facesContext.getViewRoot();
    root.addComponentResource(facesContext, this);
  }

  public abstract String getCharset();

  public abstract String getHttpEquiv();

  public abstract String getName();

  public abstract String getLang();

  public abstract String getContent();
}
