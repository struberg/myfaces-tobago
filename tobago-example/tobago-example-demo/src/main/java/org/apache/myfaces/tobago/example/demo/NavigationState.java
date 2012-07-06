package org.apache.myfaces.tobago.example.demo;

/*
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
 */

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@WindowScoped
@Named
public class NavigationState implements Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(NavigationState.class);

  @Inject
  private NavigationTree tree;

  private NavigationNode currentNode;

//  private TreeState state = new TreeState(new ExpandedState(1), new MarkedState());

  @PostConstruct
  public void init() {
    currentNode = tree.getTree().getNextNode();
    initState();
  }

  private void initState() {
//    state.getMarkedState().setMarked(currentNode.getTreePath());
//    state.getExpandedState().expand(currentNode.getTreePath());
  }

  public NavigationNode getCurrentNode() {
    return currentNode;
  }

  public String gotoFirst() {
    return gotoNode(tree.getTree().getNextNode()); // the "first" is the first after the root node
  }

  public String gotoPrevious() {
    final NavigationNode previousNode = currentNode.getPreviousNode();
    if (previousNode != null) {
      return gotoNode(previousNode);
    } else {
      LOG.warn("Strange navigation behavior");
      return null;
    }
  }

  public String gotoNext() {
    final NavigationNode nextNode = currentNode.getNextNode();
    if (nextNode != null) {
      return gotoNode(nextNode);
    } else {
      LOG.warn("Strange navigation behavior");
      return null;
    }
  }

  public void gotoNode(@Observes NavigationEvent event) {
    gotoNode(event.getCurrent());
  }

  public String gotoNode(NavigationNode node) {
    currentNode = node;
    initState();
    LOG.info("Navigate to '" + currentNode.getOutcome() + "'");
    return currentNode.getOutcome();
  }

  public boolean isFirst() {
    final NavigationNode previousNode = currentNode.getPreviousNode();
    return previousNode == null || previousNode.isRoot();
  }

  public boolean isLast() {
    final NavigationNode nextNode = currentNode.getNextNode();
    return nextNode == null;
  }

//  public TreeState getState() {
//    return state;
//  }
}