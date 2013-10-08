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

package org.apache.myfaces.tobago.taglib.decl;

import org.apache.myfaces.tobago.apt.annotation.TagAttribute;
import org.apache.myfaces.tobago.apt.annotation.UIComponentTagAttribute;
import org.apache.myfaces.tobago.apt.annotation.DynamicExpression;

/**
 * $Id$
 */
public interface HasAction {
  /**
   * Action to invoke when clicked.
   * This must be a MethodBinding or a String representing the application action to invoke when
   * this component is activated by the user.
   * The MethodBinding must evaluate to a public method that takes no parameters,
   * and returns a String (the logical outcome) which is passed to the
   * NavigationHandler for this application.
   * The String is directly passed to the Navigationhandler.
   */
  @TagAttribute
  @UIComponentTagAttribute(type = "javax.faces.el.MethodBinding", expression = DynamicExpression.METHOD_BINDING)
  void setAction(String action);

}