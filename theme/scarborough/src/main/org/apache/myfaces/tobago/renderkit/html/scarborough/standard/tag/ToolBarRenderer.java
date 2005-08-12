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
  * All rights reserved. Created 28.04.2003 at 15:29:36.
  * $Id$
  */
package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.TobagoConstants;
import org.apache.myfaces.tobago.util.AccessKeyMap;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.context.ResourceManagerUtil;
import org.apache.myfaces.tobago.renderkit.CommandRendererBase;
import org.apache.myfaces.tobago.renderkit.LabelWithAccessKey;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.renderkit.RenderUtil;
import org.apache.myfaces.tobago.renderkit.html.HtmlRendererUtil;
import org.apache.myfaces.tobago.taglib.component.ToolBarTag;
import org.apache.myfaces.tobago.taglib.component.ToolBarSelectBooleanTag;
import org.apache.myfaces.tobago.taglib.component.SelectOneCommandTag;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ToolBarRenderer extends RendererBase {

// ------------------------------------------------------------------ constants

  private static final Log LOG = LogFactory.getLog(ToolBarRenderer.class);

// ----------------------------------------------------------------- interfaces


// ---------------------------- interface TobagoRenderer

  public void encodeEndTobago(FacesContext facesContext,
      UIComponent uiComponent) throws IOException {
    UIPanel toolbar = (UIPanel) uiComponent;

    TobagoResponseWriter writer
        = (TobagoResponseWriter) facesContext.getResponseWriter();
    boolean suppressContainer = ComponentUtil.getBooleanAttribute(toolbar,
        TobagoConstants.ATTR_SUPPPRESS_TOOLBAR_CONTAINER);

    if (!suppressContainer) {
      setToolBarHeight(facesContext, uiComponent);

      writer.startElement("div", toolbar);
      writer.writeIdAttribute(toolbar.getClientId(facesContext));
      writer.writeComponentClass();
      writer.writeAttribute("style", null, TobagoConstants.ATTR_STYLE);
      writer.startElement("div", toolbar);
      writer.writeClassAttribute("tobago-toolbar-div-inner");
    }

    boolean boxFacet = isBoxFacet(toolbar);

    List children = toolbar.getChildren();

    int index = 0;
    for (Iterator iter = children.iterator(); iter.hasNext();) {
      UIComponent component = (UIComponent) iter.next();
      if (component instanceof UICommand) {
        boolean addExtraClass = boxFacet ? !iter.hasNext() : index++ == 0;
        renderToolbarCommand(facesContext, (UICommand) component, writer,
            boxFacet, addExtraClass);
      } else {
        LOG.error("Illegal UIComponent class in toolbar :"
            + component.getClass().getName());
      }
    }

    if (!suppressContainer) {
      writer.endElement("div");
      writer.endElement("div");
    }
  }

// ----------------------------------------------------------- business methods

  public void encodeChildren(FacesContext facesContext, UIComponent component)
      throws IOException {
  }

  public boolean getRendersChildren() {
    return true;
  }

  private boolean isBoxFacet(UIComponent component) {
    return (RENDERER_TYPE_BOX.equals(component.getParent().getRendererType())
        && component.getParent().getFacet(FACET_TOOL_BAR) == component);
  }

  private void renderToolbarCommand(FacesContext facesContext,
      final UICommand command, TobagoResponseWriter writer, boolean boxFacet,
      boolean addExtraHoverClass)
      throws IOException {
    if (ToolBarSelectBooleanTag.COMMAND_TYPE.equals(
        command.getAttributes().get(ATTR_COMMAND_TYPE))) {
      renderSelectBoolean(facesContext, command, writer, boxFacet, addExtraHoverClass);
    } else if (SelectOneCommandTag.COMMAND_TYPE.equals(
        command.getAttributes().get(ATTR_COMMAND_TYPE))) {
      renderSelectOne(facesContext, command, writer, boxFacet, addExtraHoverClass);
    } else {

      String onClick = createOnClick(facesContext, command);
      renderToolbarButton(facesContext, command, writer, boxFacet, addExtraHoverClass, false, onClick);
    }

  }

  private void renderSelectOne(FacesContext facesContext, UICommand command,
      TobagoResponseWriter writer, boolean boxFacet, boolean addExtraHoverClass)
      throws IOException {

    String onClick = createOnClick(facesContext, command);
    onClick = CommandRendererBase.appendConfirmationScript(onClick, command,
        facesContext);


    List<SelectItem> items = ComponentUtil.getSelectItems(command);

    UISelectOne radio = (UISelectOne) command.getFacet(FACET_RADIO);
    if (radio == null) {
      radio = ComponentUtil.createUISelectOneFacet(facesContext, command);
    }


    if (radio != null) {
      Object value = ((ValueHolder) radio).getValue();

      boolean markFirst = !ComponentUtil.hasSelectedValue(items, value);
      String radioId = radio.getClientId(facesContext);
      String onClickPrefix = "menuSetRadioValue('" + radioId + "', '";
      String onClickPostfix = onClick != null ? "') ; " + onClick : "";
      for (SelectItem item : items) {
        final String labelText = item.getLabel();
        if (labelText != null) {
          if (labelText.indexOf(LabelWithAccessKey.INDICATOR) > -1) {
            command.getAttributes().put(ATTR_LABEL_WITH_ACCESS_KEY, labelText);
          } else {
            command.getAttributes().put(ATTR_LABEL, labelText);
          }
        } else {
          LOG.warn("Menu item has label=null. UICommand.getClientId()="
              + command.getClientId(facesContext));
        }

        String image = null;
        if (item instanceof org.apache.myfaces.tobago.model.SelectItem) {
          image = ((org.apache.myfaces.tobago.model.SelectItem)item).getImage();
        } else if (LOG.isDebugEnabled()) {
          LOG.debug("select item is not " + org.apache.myfaces.tobago.model.SelectItem.class.getName());
        }
        if (image == null) {
          image = "image/1x1.gif";
        }
        command.getAttributes().put(ATTR_IMAGE, image);

        if (item.getDescription() != null) {
          command.getAttributes().put(ATTR_TIP, item.getDescription());
        }


        String formattedValue
            = getFormattedValue(facesContext, command, item.getValue());
        onClick = onClickPrefix + formattedValue + onClickPostfix;
        final boolean checked;
        if (item.getValue().equals(value) || markFirst) {
          checked = true;
          markFirst = false;
          HtmlRendererUtil.startJavascript(writer);
          writer.write("    " + onClickPrefix + formattedValue + "');");
          HtmlRendererUtil.endJavascript(writer);
        } else {
          checked = false;
        }

        renderToolbarButton(facesContext, command, writer, boxFacet, addExtraHoverClass, checked, onClick);

      }
    }

  }

  private void renderSelectBoolean(FacesContext facesContext, UICommand command,
      TobagoResponseWriter writer, boolean boxFacet, boolean addExtraHoverClass)
      throws IOException {

    UIComponent checkbox = command.getFacet(FACET_CHECKBOX);
    if (checkbox == null) {
      checkbox = ComponentUtil.createUISelectBooleanFacet(facesContext, command);
    }

    final boolean checked = ComponentUtil.getBooleanAttribute(command, ATTR_VALUE);

    String onClick = createOnClick(facesContext, command);

    if (checkbox != null) {
      String clientId = checkbox.getClientId(facesContext);
      onClick = RenderUtil.addMenuCheckToggle(clientId, onClick);
      if (checked) {
        HtmlRendererUtil.startJavascript(writer);
        writer.write("    menuCheckToggle('" + clientId + "');\n");
        HtmlRendererUtil.endJavascript(writer);
      }
    }

    renderToolbarButton(facesContext, command, writer, boxFacet, addExtraHoverClass, checked, onClick);
  }

  private void renderToolbarButton(FacesContext facesContext,
                                   final UICommand command, TobagoResponseWriter writer, boolean boxFacet,
                                   boolean addExtraHoverClass, boolean selected, String onClick)
      throws IOException {
    if (!command.isRendered()) {
      return;
    }

    final String clientId = command.getClientId(facesContext);
    final boolean disabled = ComponentUtil.getBooleanAttribute(command,
        ATTR_DISABLED);
    final LabelWithAccessKey label = new LabelWithAccessKey(command);
    final UIComponent popupMenu = command.getFacet(FACET_MENUPOPUP);

    Map parentAttributes = command.getParent().getAttributes();
    String labelPosition = (String) parentAttributes.get(ATTR_LABEL_POSITION);
    String iconSize = (String) parentAttributes.get(ATTR_ICON_SIZE);

    onClick = CommandRendererBase.appendConfirmationScript(onClick, command,
        facesContext);

    String divClasses = "tobago-toolbar-button"
        + " tobago-toolbar-button-"  + (boxFacet ? "box-facet-" : "")
        + (selected ? "selected-" : "") + (disabled ? "disabled" : "enabled")
        + (boxFacet ? " tobago-toolbar-button-box-facet" : "");

    String tableClasses = "tobago-toolbar-button-table"
        + " tobago-toolbar-button-table-" + (boxFacet ? "box-facet-" : "")
        + (selected ? "selected-" : "") + (disabled ? "disabled" : "enabled")
        + (boxFacet ? " tobago-toolbar-button-table-box-facet" : "");


    String iconName = (String) command.getAttributes().get(ATTR_IMAGE);
    String image = getImage(facesContext, iconName, iconSize, disabled, selected);
    String graphicId = clientId + SUBCOMPONENT_SEP + "icon";

    String extraHoverClass = "";
    if (addExtraHoverClass == true) {
      if (!boxFacet) {
        extraHoverClass = " tobago-toolBar-button-hover-first";
      } else {
        extraHoverClass = " tobago-box-toolBar-button-hover-last";
      }
    }
    final String args = "this, 'tobago-toolBar-button-hover"
        + (boxFacet ? " tobago-toolBar-button-box-facet-hover" : "")
        + extraHoverClass + "', '" + graphicId + "'";
    final String mouseOverScript = "tobagoToolbarMousesover(" + args + ");";
    final String mouseOutScript = "tobagoToolbarMousesout(" + args + ");";

    writer.startElement("div", null);
    writer.writeClassAttribute(divClasses);
    if (!disabled) {
      writer.writeAttribute("onmouseover", mouseOverScript, null);
      writer.writeAttribute("onmouseout", mouseOutScript, null);
      writer.writeAttribute("onclick", onClick, null);
    }
    writer.startElement("table", null);
    writer.writeAttribute("cellpadding", "0", null);
    writer.writeAttribute("cellspacing", "0", null);
    writer.writeAttribute("summary", "", null);
    writer.writeAttribute("border", "0", null);
    writer.writeClassAttribute(tableClasses);
    writer.startElement("tr", null);


    boolean anchorOnLabel =
        label.getText() != null && !ToolBarTag.LABEL_OFF.equals(labelPosition);


    if (!ToolBarTag.ICON_OFF.equals(iconSize)) {
      if (iconName != null) {
        ImageRenderer.addImageSources(facesContext,
            ComponentUtil.findPage(command), iconName, graphicId);
      }

      writer.startElement("td", command);
      writer.writeAttribute("align", "center", null);
      writer.writeAttribute("title", null, ATTR_TIP);

      boolean render1pxImage
          = (iconName == null && (!ToolBarTag.LABEL_BOTTOM.equals(
              labelPosition)
          && label.getText() != null));


      if (((!ToolBarTag.LABEL_OFF.equals(labelPosition) &&
          label.getText() != null)
          || popupMenu != null) && !render1pxImage) {
        writer.writeAttribute("style", "padding-right: 3px;", null);
        // todo: make this '3px' configurable
      }

      String className = "tobago-image-default tobago-toolBar-button-image"
          + " tobago-toolBar-button-image-" + iconSize;

      if (!anchorOnLabel) {
        renderAnchorBegin(facesContext, writer, command, label, disabled);
      }
      writer.startElement("img", command);
      writer.writeIdAttribute(graphicId);
      writer.writeAttribute("src", image, null);
      writer.writeAttribute("alt", "", null);
      writer.writeAttribute("title", null, ATTR_TIP);
      writer.writeAttribute("border", "0", null);
      writer.writeClassAttribute(className);
      if (render1pxImage) {
        writer.writeAttribute("style", "width: 1px;", null);
      }

      writer.endElement("img");
      if (!anchorOnLabel) {
        writer.endElement("a");
      }
      writer.endElement("td");
    }

    boolean popupOn2 = ToolBarTag.LABEL_BOTTOM.equals(labelPosition)
        && !ToolBarTag.ICON_OFF.equals(iconSize);
    if (popupOn2) {
      if (popupMenu != null) {
        renderPopupTd(facesContext, writer, command, popupMenu,
            true);
      }
      writer.endElement("tr");
      writer.startElement("tr", null);
    }

    if (!ToolBarTag.LABEL_OFF.equals(labelPosition)) {
      writer.startElement("td", null);
      writer.writeClassAttribute("tobago-toolbar-label-td");
      writer.writeAttribute("align", "center", null);
      if (popupMenu != null) {
        writer.writeAttribute("style", "padding-right: 3px;", null);
        // todo: make this '3px' configurable
      }

      if (label.getText() != null) {
        renderAnchorBegin(facesContext, writer, command, label, disabled);
        HtmlRendererUtil.writeLabelWithAccessKey(writer, label);
        writer.endElement("a");
      }
      writer.endElement("td");
    }


    if (!popupOn2 && popupMenu != null) {
      renderPopupTd(facesContext, writer, command, popupMenu,
          false);
    }

    writer.endElement("tr");
    writer.endElement("table");
    writer.endElement("div");
  }

  private String createOnClick(FacesContext facesContext,
      UIComponent component) {
    return ButtonRenderer.createOnClick(facesContext, component);
//    String type = (String) component.getAttributes().get(ATTR_TYPE);
//    String command = (String) component.getAttributes().get(ATTR_ACTION_STRING);
//    String clientId = component.getClientId(facesContext);
//    String onclick;
//
//    if (COMMAND_TYPE_NAVIGATE.equals(type)) {
//      onclick = "navigateToUrl('"
//          + HtmlUtils.generateUrl(facesContext, command) + "')";
//    } else if (COMMAND_TYPE_RESET.equals(type)) {
//      onclick = null;
//    } else if (COMMAND_TYPE_SCRIPT.equals(type)) {
//      onclick = command;
//    } else { // default: Action.TYPE_SUBMIT
//      onclick = "submitAction('" +
//          ComponentUtil.findPage(component).getFormId(facesContext) +
//          "','" + clientId + "')";
//    }
//    return onclick;
  }

  private String getImage(FacesContext facesContext, String name,
                          String iconSize, boolean disabled, boolean selected) {
    if (name == null) {
      return ResourceManagerUtil.getImage(facesContext, "image/1x1.gif");
    }
    int pos = name.lastIndexOf('_');
    if (pos == -1) {
      pos = name.lastIndexOf('.');
    }
    if (pos == -1) {
      pos = name.length(); // avoid exception if no '_' or '.' in name
    }
    String key = name.substring(0, pos);
    String ext = name.substring(pos);

    String size = "";
    if (ToolBarTag.ICON_SMALL.equals(iconSize)) {
      size = "16";
    } else if (ToolBarTag.ICON_BIG.equals(iconSize)) {
      size = "32";
    }
    String image = null;
    if (disabled && selected) {
      image = ResourceManagerUtil.getImage(
          facesContext, key + "SelectedDisabled" + size + ext, true);
      if (image == null) {
        image = ResourceManagerUtil.getImage(
                facesContext, key + "SelectedDisabled" + ext, true);
      }
    }
    if (image == null && disabled) {
      image = ResourceManagerUtil.getImage(
          facesContext, key + "Disabled" + size + ext, true);
      if (image == null) {
        image = ResourceManagerUtil.getImage(
            facesContext, key + "Disabled" + ext, true);
      }
    }
    if (image == null && selected) {
      image = ResourceManagerUtil.getImage(
          facesContext, key + "Selected" + size + ext, true);
      if (image == null) {
        image = ResourceManagerUtil.getImage(
            facesContext, key + "Selected" + ext, true);
      }
    }
    if (image == null) {
      image
          = ResourceManagerUtil.getImage(facesContext, key + size + ext, true);
      if (image == null) {
        image = ResourceManagerUtil.getImage(facesContext, key + ext, true);
      }
    }
    return image;
  }

  private void renderAnchorBegin(FacesContext facesContext,
      TobagoResponseWriter writer, final UICommand command,
      final LabelWithAccessKey label, final boolean disabled)
      throws IOException {
    writer.startElement("a", command);
    writer.writeClassAttribute("tobago-toolBar-button-link");
    writer.writeAttribute("title", null, ATTR_TIP);
    if (!disabled) {
      writer.writeAttribute("href", "#", null);
      writer.writeAttribute("onfocus", "tobagoToolbarFocus(this, event)", null);
      if (label.getAccessKey() != null) {
        if (LOG.isInfoEnabled()
                && ! AccessKeyMap.addAccessKey(facesContext, label.getAccessKey())) {
          LOG.info("dublicated accessKey : " + label.getAccessKey());
        }
        writer.writeAttribute("accesskey", label.getAccessKey(), null);
      }
    }
  }

  private void renderPopupTd(FacesContext facesContext,
      TobagoResponseWriter writer, UIComponent command, UIComponent popupMenu,
      boolean labelBottom)
      throws IOException {
    writer.startElement("td", null);
    if (labelBottom) {
      writer.writeAttribute("rowspan", "2", null);
    }

    if (popupMenu != null) {
      String backgroundImage = ResourceManagerUtil.getImage(facesContext,
          "image/1x1.gif");
      writer.startElement("div", null);
      writer.writeIdAttribute(
          command.getClientId(facesContext) + SUBCOMPONENT_SEP + "popup");
      writer.writeClassAttribute("tobago-toolBar-button-menu");
      writer.startElement("img", null);
      writer.writeAttribute("src", backgroundImage, null);
      writer.writeClassAttribute("tobago-toolBar-button-menu-background-image");
      writer.endElement("img");
      writer.endElement("div");
      popupMenu.getAttributes().put(ATTR_MENU_POPUP, Boolean.TRUE);
      popupMenu.getAttributes().put(ATTR_MENU_POPUP_TYPE, "ToolBarButton");
      popupMenu.setRendererType(RENDERER_TYPE_MENUBAR);
      popupMenu.getAttributes().remove(ATTR_LABEL);
      popupMenu.getAttributes().remove(ATTR_LABEL_WITH_ACCESS_KEY);
      popupMenu.getAttributes().put(ATTR_IMAGE, "image/toolbarButtonMenu.gif");
      RenderUtil.encode(facesContext, popupMenu);
    }

    writer.endElement("td");
  }

  private void setToolBarHeight(FacesContext facesContext,
      UIComponent component) {
    final int height = getFixedHeight(facesContext, component);
    final Map attributes = component.getAttributes();
    String style = HtmlRendererUtil.replaceStyleAttribute((String)
        attributes.get(ATTR_STYLE), "height", Integer.toString(height) + "px");
    attributes.put(ATTR_STYLE, style);
  }

  public int getFixedHeight(FacesContext facesContext, UIComponent component) {
    final Map attributes = component.getAttributes();
    final String labelPosition = (String) attributes.get(ATTR_LABEL_POSITION);
    final String iconSize = (String) attributes.get(ATTR_ICON_SIZE);

    final String key = iconSize + "_" + labelPosition + "_Height";
    return getConfiguredValue(facesContext, component, key);
  }
}
