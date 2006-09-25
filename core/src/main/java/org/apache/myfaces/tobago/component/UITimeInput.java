package org.apache.myfaces.tobago.component;

/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import static javax.faces.convert.DateTimeConverter.CONVERTER_ID;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.util.TimeZone;

/*
 * Created by IntelliJ IDEA.
 * User: bommel
 * Date: 10.02.2006
 * Time: 20:50:49
 */
public class UITimeInput extends javax.faces.component.UIInput {

  public static final String COMPONENT_TYPE = "org.apache.myfaces.tobago.TimeInput";

  public Converter getConverter() {
    Converter converter = super.getConverter();
    if (converter == null) {
      // setting required default converter
      Application application
          = FacesContext.getCurrentInstance().getApplication();
      DateTimeConverter dateTimeConverter
          = (DateTimeConverter) application.createConverter(CONVERTER_ID);
      dateTimeConverter.setPattern("HH:mm");
      dateTimeConverter.setTimeZone(TimeZone.getDefault());
      setConverter(dateTimeConverter);
    }
    return converter;
  }
}
