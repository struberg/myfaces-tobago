<%--
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>

<%@ taglib uri="http://myfaces.apache.org/tobago/sandbox" prefix="tcs" %>
<%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
  <tc:loadBundle basename="demo" var="bundle"/>

  <tc:page label="Sandbox - InputSlider" width="500px" height="800px">
    <f:facet name="layout">
      <tc:gridLayout margin="10px" rows="*"/>
    </f:facet>
    <tc:box label="InputSlider (problems: scriptaculous was removed, no layout manager)">
      <f:facet name="layout">
        <tc:gridLayout columns="3*;*" rows="auto;*"/>
      </f:facet>
      <tcs:numberSlider value="#{controller.sliderValue}" min="0" max="200">
      </tcs:numberSlider>
      <tc:button action="#{controller.sliderSubmit}" label="Submit" />
      <tc:cell/>
      <tc:cell/>
    </tc:box>
  </tc:page>
</f:view>