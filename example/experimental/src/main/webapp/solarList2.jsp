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
--%><%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
  <tc:page width="750px" height="300px">
    <f:facet name="layout">
      <tc:gridLayout rows="1*;20px"/>
    </f:facet>
    <tc:box >
       <tc:sheet binding="#{test.table}" value="#{test.solarObjects}"
            columns="1*;1*;1*;1*;1*;1*;1*;1*;2*" var="solarObject"
            showHeader="true"  showPageRange="center" rows="10" >
        </tc:sheet>
    </tc:box>
    <tc:panel>
      <f:facet name="layout">
        <tc:gridLayout columns="1*;fixed;1*"/>
      </f:facet>
      <tc:cell/>
      <tc:button action="#{test.export}" label="Export" transition="false" />
      <tc:cell/>
    </tc:panel>

  </tc:page>
</f:view>