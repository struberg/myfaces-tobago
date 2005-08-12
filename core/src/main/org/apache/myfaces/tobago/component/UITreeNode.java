/*
 * Copyright 2002-2005 atanion GmbH.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/*
  * All rights reserved. Created 06.01.2003 at 15:33:42.
  * $Id$
  */
package org.apache.myfaces.tobago.component;

import org.apache.myfaces.tobago.TobagoConstants;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class UITreeNode extends UIInput {

// ///////////////////////////////////////////// constant

  private static final Log LOG = LogFactory.getLog(UITreeNode.class);

// ///////////////////////////////////////////// attribute

  private String subReference;

// ///////////////////////////////////////////// constructor

  protected UITreeNode(UIComponent parent, int index) {
    super();
    if (parent instanceof UITreeNode) {
      String parentSubReference = ((UITreeNode) parent).getSubReference();
      if (parentSubReference == null) {
        subReference = "childAt[" + index + "]";
      } else {
        subReference = parentSubReference + ".childAt[" + index + "]";
      }
    }
    setRendererType(TobagoConstants.RENDERER_TYPE_TREE_NODE);
    parent.getChildren().add(this);
    initId();
    initName();
  }

  public UITreeNode() {
  }

// ///////////////////////////////////////////// code

  public boolean getRendersChildren() {
    return true;
  }

  public String getSubReference() {
    return subReference;
  }

  public DefaultMutableTreeNode getTreeNode() {
    return (DefaultMutableTreeNode) getValue();
  }

  public Object getValue() {
    TreeNode value = null;
    UITree root = findTreeRoot();
    if (LOG.isDebugEnabled()) {
      LOG.debug("root         = '" + root + "'");
      LOG.debug("subReference = '" + subReference + "'");
    }
    TreeNode rootNode = (TreeNode) root.getValue();
    if (LOG.isDebugEnabled()) {
      LOG.debug("rootNode = '" + rootNode + "'");
    }
    if (rootNode != null) {
      try {
        if (subReference == null) {
          value = rootNode;
        } else {
          value = (TreeNode) PropertyUtils.getProperty(rootNode, subReference);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("treeNode     = '" + value + "'");
        }
      } catch (Throwable e) {
        LOG.error("subReference = '" + subReference + "'", e);
      }
    }
    return value;
  }

  protected void createTreeNodes() {

    TreeNode node = (TreeNode) getValue();
    if (node != null) {
      int childCount = node.getChildCount();
      for (int i = 0; i < childCount; i++) {
        UITreeNode component = new UITreeNode(this, i);
        component.createTreeNodes();
      }
    }
  }

  private void initName() {
    String name = null;
    UITree root = findTreeRoot();
    TreeNode treeNode = (TreeNode) getValue();
    if (treeNode != null) {
      String nameReference
          = (String) root.getAttributes().get(
              TobagoConstants.ATTR_NAME_REFERENCE);
      if (nameReference != null) {
        try {
          name = BeanUtils.getProperty(treeNode, nameReference);
        } catch (Exception e) {
          LOG.warn(
              "Can't find name over ref='" + nameReference
              + "' treeNode='" + treeNode + "!", e);
        }
      }
      if (name == null) {
        name = toString();
      }
      getAttributes().put(TobagoConstants.ATTR_NAME, name);
    }
  }

  private void initId() {
    String id = null;
    UITree root = findTreeRoot();
    TreeNode treeNode = (TreeNode) getValue();
    if (treeNode != null) {
      String idReference
          = (String) root.getAttributes().get(
              TobagoConstants.ATTR_ID_REFERENCE);
      if (idReference != null) {
        try {
          id = BeanUtils.getProperty(treeNode, idReference);
        } catch (Exception e) {
          LOG.warn(
              "Can't find id over ref '" + idReference
              + "' treeNode='" + treeNode + "!", e);
        }
      }
      if (id == null) {
        id = "node" + Integer.toString(System.identityHashCode(treeNode));
      }
      setId(id);
    }
  }

  public UITree findTreeRoot() {
    UIComponent ancestor = getParent();
    while (ancestor != null && ancestor instanceof UITreeNode) {
      ancestor = ancestor.getParent();
    }
    if (ancestor instanceof UITree) {
      return (UITree) ancestor;
    }
    return null;
  }

  public void updateModel(FacesContext facesContext) {
    // nothig to update for treeNode's
  }

  protected Object checkValue(Object currentValue) {

    if (currentValue == null) {
      LOG.error("currentValue is null: '" + currentValue + "'");
      currentValue = emergencyValue();
    }

    if (!(currentValue instanceof MutableTreeNode)) {
      LOG.error("currentValue is not valid: '" + currentValue + "'");
      LOG.error(
          "currentValue is not of type '"
          + MutableTreeNode.class.getName() + "': '"
          + currentValue.getClass().getName() + "'");
      currentValue = emergencyValue();
    }

    return currentValue;
  }

  protected Object emergencyValue() {
    return new DefaultMutableTreeNode("Default");
  }


// ///////////////////////////////////////////// bean getter + setter

}

