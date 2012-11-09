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

package org.apache.myfaces.tobago.example.sandbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Node {

  private static final Log LOG = LogFactory.getLog(Node.class);

  private String name;
  private String markup;
  private boolean expanded;
  private boolean disabled;

  public Node(String name) {
    this.name = name;
  }

  public Node(String name, String markup) {
    this.name = name;
    this.markup = markup;
  }

  public Node(String name, boolean disabled) {
    this.name = name;
    this.disabled = disabled;
  }

  public String action() {
    LOG.info("action: name='" + name + "'");
    return null;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMarkup() {
    return markup;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  public String getTip() {
    return "Some Information about " + name;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}