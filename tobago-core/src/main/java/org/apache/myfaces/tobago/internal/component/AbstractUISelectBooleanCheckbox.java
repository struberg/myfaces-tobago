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

import org.apache.myfaces.tobago.component.LabelLayout;
import org.apache.myfaces.tobago.component.SupportFieldId;
import org.apache.myfaces.tobago.component.SupportsAccessKey;
import org.apache.myfaces.tobago.component.SupportsLabelLayout;
import org.apache.myfaces.tobago.component.Visual;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.util.MessageUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

/**
 * {@link org.apache.myfaces.tobago.internal.taglib.component.SelectBooleanCheckboxTagDeclaration}
 */
public abstract class AbstractUISelectBooleanCheckbox extends UISelectBoolean
    implements Visual, ClientBehaviorHolder, SupportFieldId, SupportsAccessKey, SupportsLabelLayout {

  @Override
  public boolean isSelected() {
    Object value = getSubmittedValue();
    if (value == null) {
      value = getValue();
    }
    if (value instanceof Boolean) {
      return (Boolean) value;
    } else {
      return value != null && Boolean.valueOf(value.toString());
    }
  }

  @Override
  protected void validateValue(final FacesContext facesContext, final Object convertedValue) {
    if (isRequired()) {
      if (convertedValue instanceof Boolean && !((Boolean) convertedValue)
          // String: e. g. if there is no ValueExpression
          || convertedValue instanceof String && !Boolean.parseBoolean((String) convertedValue)) {
        MessageUtils.addMessage(
            facesContext, this, FacesMessage.SEVERITY_ERROR, REQUIRED_MESSAGE_ID, new Object[]{getId()});
        setValid(false);
        return;
      }
    }
    super.validateValue(facesContext, convertedValue);
  }

  public abstract boolean isDisabled();

  public abstract boolean isReadonly();

  public abstract boolean isFocus();

  public abstract Integer getTabIndex();

  @Override
  public abstract String getLabel();

  public abstract String getItemLabel();

  public abstract void setItemLabel(String itemLabel);

  @Override
  public String getFieldId(final FacesContext facesContext) {
    return getClientId(facesContext) + ComponentUtils.SUB_SEPARATOR + "field";
  }

  public boolean isLabelLayoutSkip() {
    return getLabelLayout() == LabelLayout.skip;
  }
}
