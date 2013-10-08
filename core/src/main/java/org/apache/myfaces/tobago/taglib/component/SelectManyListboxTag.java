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

package org.apache.myfaces.tobago.taglib.component;

import org.apache.myfaces.tobago.component.UISelectMany;
import org.apache.myfaces.tobago.util.Deprecation;

import javax.faces.component.UIComponent;

public class SelectManyListboxTag extends SelectManyTag implements SelectManyListboxTagDeclaration {

  public String getComponentType() {
    return UISelectMany.COMPONENT_TYPE;
  }

  protected void setProperties(UIComponent component) {
    if (getLabel() != null && Deprecation.LOG.isErrorEnabled()) {
      Deprecation.LOG.error(
          "the label attribute is deprecated in tc:selectManyListbox, please use tx:selectManyListbox instead.");
    }
    super.setProperties(component);
  }
}