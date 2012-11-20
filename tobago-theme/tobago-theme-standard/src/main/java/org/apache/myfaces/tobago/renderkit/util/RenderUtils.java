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

package org.apache.myfaces.tobago.renderkit.util;

import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.config.Configurable;
import org.apache.myfaces.tobago.context.ResourceManagerUtils;
import org.apache.myfaces.tobago.internal.component.AbstractUICommandBase;
import org.apache.myfaces.tobago.internal.component.AbstractUIData;
import org.apache.myfaces.tobago.internal.component.AbstractUITree;
import org.apache.myfaces.tobago.internal.util.StringUtils;
import org.apache.myfaces.tobago.layout.Measure;
import org.apache.myfaces.tobago.model.ExpandedState;
import org.apache.myfaces.tobago.model.MarkedState;
import org.apache.myfaces.tobago.model.TreePath;
import org.apache.myfaces.tobago.renderkit.RendererBase;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.util.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RenderUtils {

  private static final Logger LOG = LoggerFactory.getLogger(RenderUtils.class);

  public static final String COMPONENT_IN_REQUEST = "org.apache.myfaces.tobago.component";

  private RenderUtils() {
    // to prevent instantiation
  }

  public static boolean contains(Object[] list, Object value) {
    if (list == null) {
      return false;
    }
    for (Object aList : list) {
      if (aList != null && aList.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static void encodeChildren(FacesContext facesContext, UIComponent panel) throws IOException {
    for (UIComponent child : (List<UIComponent>) panel.getChildren()) {
      encode(facesContext, child);
    }
  }

  public static void encode(FacesContext facesContext, UIComponent component) throws IOException {
    encode(facesContext, component, null);
  }

  public static void encode(
      FacesContext facesContext, UIComponent component, List<? extends Class<? extends UIComponent>> only)
      throws IOException {

    if (only != null && !matchFilter(component, only)) {
      return;
    }

    if (component.isRendered()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("rendering " + component.getRendererType() + " " + component);
      }
      component.encodeBegin(facesContext);
      if (component.getRendersChildren()) {
        component.encodeChildren(facesContext);
      } else {
        for (Object o : component.getChildren()) {
          UIComponent kid = (UIComponent) o;
          encode(facesContext, kid, only);
        }
      }
      component.encodeEnd(facesContext);
    }
  }

  private static boolean matchFilter(UIComponent component, List<? extends Class<? extends UIComponent>> only) {
    for (Class clazz : only) {
      if (clazz.isAssignableFrom(component.getClass())) {
        return true;
      }
    }
    return false;
  }

  public static void prepareRendererAll(FacesContext facesContext, UIComponent component) throws IOException {
    if (!component.isRendered()) {
      return;
    }
    RendererBase renderer = ComponentUtils.getRenderer(facesContext, component);
    boolean prepareRendersChildren = false;
    if (renderer != null) {
      renderer.prepareRender(facesContext, component);
      prepareRendersChildren = renderer.getPrepareRendersChildren();
    }
    if (prepareRendersChildren) {
      renderer.prepareRendersChildren(facesContext, component);
    } else {
      Iterator it = component.getFacetsAndChildren();
      while (it.hasNext()) {
        UIComponent child = (UIComponent) it.next();
        prepareRendererAll(facesContext, child);
      }
    }
  }

  public static String getFormattedValue(
      FacesContext facesContext, UIComponent component) {
    Object value = null;
    if (component instanceof ValueHolder) {
      value = ((ValueHolder) component).getLocalValue();
      if (value == null) {
        value = ((ValueHolder) component).getValue();
      }
    }
    return getFormattedValue(facesContext, component, value);
  }

  // Copy from RendererBase
  public static String getFormattedValue(
      FacesContext context, UIComponent component, Object currentValue)
      throws ConverterException {

    if (currentValue == null) {
      return "";
    }

    if (!(component instanceof ValueHolder)) {
      return currentValue.toString();
    }

    Converter converter = ((ValueHolder) component).getConverter();

    if (converter == null) {
      if (currentValue instanceof String) {
        return (String) currentValue;
      }
      Class converterType = currentValue.getClass();
      converter = context.getApplication().createConverter(converterType);
    }

    if (converter == null) {
      return currentValue.toString();
    } else {
      return converter.getAsString(context, component, currentValue);
    }
  }

  public static Measure calculateStringWidth(FacesContext facesContext, UIComponent component, String text) {
    return calculateStringWidth(facesContext, (Configurable) component, text, "tobago.font.widths");
  }

  public static Measure calculateStringWidth2(FacesContext facesContext, UIComponent component, String text) {
    return calculateStringWidth(facesContext, (Configurable) component, text, "tobago.font2.widths");
  }

  private static Measure calculateStringWidth(
      FacesContext facesContext, Configurable component, String text, String type) {
    if (text == null) {
      return Measure.ZERO;
    }
    int width = 0;
    int defaultCharWidth = 10;
    try {
      defaultCharWidth = ResourceManagerUtils.getThemeMeasure(facesContext, component, "fontWidth").getPixel();
    } catch (NullPointerException e) {
      LOG.warn("no value for 'fontWidth' for type '" + component.getRendererType() + "' found in theme-config");
    }

    String fontWidths = ResourceManagerUtils.getProperty(facesContext, "tobago", type);

    for (char c : text.toCharArray()) {
      if (c >= 32 && c < 128) { // "normal" char in precomputed range
        int begin = (c - 32) * 2;
        width += Integer.parseInt(fontWidths.substring(begin, begin + 2), 16);
      } else {
        width += defaultCharWidth;
      }
    }

    return Measure.valueOf(width);
  }

  public static List<SelectItem> getItemsToRender(javax.faces.component.UISelectOne component) {
    return getItems(component);
  }

  public static List<SelectItem> getItemsToRender(javax.faces.component.UISelectMany component) {
    return getItems(component);
  }

  public static List<SelectItem> getItems(javax.faces.component.UIInput component) {

    List<SelectItem> selectItems = getSelectItems(component);

    String renderRange = (String) component.getAttributes().get(Attributes.RENDER_RANGE_EXTERN);
    if (renderRange == null) {
      renderRange = (String) component.getAttributes().get(Attributes.RENDER_RANGE);
    }
    if (renderRange == null) {
      return selectItems;
    }

    int[] indices = StringUtils.getIndices(renderRange);
    List<SelectItem> items = new ArrayList<SelectItem>(indices.length);

    if (selectItems.size() != 0) {
      for (int indice : indices) {
        items.add(selectItems.get(indice));
      }
    } else {
      LOG.warn("No items found! rendering dummies instead!");
      for (int i = 0; i < indices.length; i++) {
        items.add(new SelectItem(Integer.toString(i), "Item " + i, ""));
      }
    }
    return items;
  }

  public static String currentValue(UIComponent component) {
    String currentValue = null;
    if (component instanceof ValueHolder) {
      Object value;
      if (component instanceof EditableValueHolder) {
        value = ((EditableValueHolder) component).getSubmittedValue();
        if (value != null) {
          return (String) value;
        }
      }

      value = ((ValueHolder) component).getValue();
      if (value != null) {
        Converter converter = ((ValueHolder) component).getConverter();
        if (converter == null) {
          FacesContext context = FacesContext.getCurrentInstance();
          converter = context.getApplication().createConverter(value.getClass());
        }
        if (converter != null) {
          currentValue =
              converter.getAsString(FacesContext.getCurrentInstance(),
                  component, value);
        } else {
          currentValue = value.toString();
        }
      }
    }
    return currentValue;
  }

  public static List<SelectItem> getSelectItems(UIComponent component) {

    ArrayList<SelectItem> list = new ArrayList<SelectItem>();

    for (Object o1 : component.getChildren()) {
      UIComponent kid = (UIComponent) o1;
      if (LOG.isDebugEnabled()) {
        LOG.debug("kid " + kid);
        LOG.debug("kid " + kid.getClass().getName());
      }
      if (kid instanceof UISelectItem) {
        Object value = ((UISelectItem) kid).getValue();
        if (value == null) {
          UISelectItem item = (UISelectItem) kid;
          if (kid instanceof org.apache.myfaces.tobago.component.UISelectItem) {
            list.add(getSelectItem(
                (org.apache.myfaces.tobago.component.UISelectItem) kid));
          } else {
            list.add(new SelectItem(item.getItemValue() == null ? "" : item.getItemValue(),
                item.getItemLabel() != null ? item.getItemLabel() : item.getItemValue().toString(),
                item.getItemDescription()));
          }
        } else if (value instanceof SelectItem) {
          list.add((SelectItem) value);
        } else {
          final String message
              = "TYPE ERROR: value NOT instanceof SelectItem. type="
              + value.getClass().getName() + " value=" + value;
          LOG.error(message);
          DebugUtils.addDevelopmentMessage(FacesContext.getCurrentInstance(), message);
        }
      } else if (kid instanceof UISelectItems) {
        Object value = ((UISelectItems) kid).getValue();
        if (LOG.isDebugEnabled()) {
          LOG.debug("value " + value);
          if (value != null) {
            LOG.debug("value " + value.getClass().getName());
          }
        }
        if (value == null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("value is null");
          }
        } else if (value instanceof SelectItem) {
          list.add((SelectItem) value);
        } else if (value instanceof SelectItem[]) {
          SelectItem[] items = (SelectItem[]) value;
          list.addAll(Arrays.asList(items));
        } else if (value instanceof Collection) {
          for (Object o : ((Collection) value)) {
            list.add((SelectItem) o);
          }
        } else if (value instanceof Map) {
          for (Object key : ((Map) value).keySet()) {
            if (key != null) {
              Object val = ((Map) value).get(key);
              if (val != null) {
                list.add(new SelectItem(val.toString(), key.toString(), null));
              }
            }
          }
        } else {
          final String message
              = "TYPE ERROR: value NOT instanceof SelectItem, SelectItem[], Collection, Map. type="
              + value.getClass().getName() + " value=" + value;
          LOG.error(message);
          DebugUtils.addDevelopmentMessage(FacesContext.getCurrentInstance(), message);
        }
      }
    }

    return list;
  }

  private static SelectItem getSelectItem(org.apache.myfaces.tobago.component.UISelectItem component) {
    return
        new org.apache.myfaces.tobago.model.SelectItem(component.getItemValue() == null ? "" : component.getItemValue(),
            component.getItemLabel(), component.getItemDescription(),
            component.isItemDisabled(), component.getItemImage(), component.getMarkup());
  }


  public static void decodedStateOfTreeData(FacesContext facesContext, AbstractUIData data) {

    if (!data.isTreeModel()) {
      return;
    }

    final boolean isTree = data instanceof AbstractUITree;
    // marked
    final Integer markedIndex = isTree ? decodeMarkedIndex(facesContext, data) : null;

    // expanded
    final List<Integer> expandedIndices = decodeExpandedIndices(facesContext, data);

    final int last = data.isRowsUnlimited() ? Integer.MAX_VALUE : data.getFirst() + data.getRows();
    for (int rowIndex = data.getFirst(); rowIndex < last; rowIndex++) {
      data.setRowIndex(rowIndex);
      if (!data.isRowAvailable()) {
        break;
      }

      final TreePath path = data.getPath();

      // marked
      if (isTree) {
        final MarkedState markedState = ((AbstractUITree) data).getMarkedState();
        final boolean oldMarked = markedState.isMarked(path);
        final boolean newMarked = ((Integer) rowIndex).equals(markedIndex);
        if (newMarked != oldMarked) {
          if (newMarked) {
            markedState.setMarked(path);
          } else {
            markedState.setMarked((TreePath) null);
          }
        }
      }

      // expanded
      if (expandedIndices != null) {
        final ExpandedState expandedState = data.getExpandedState();
        final boolean oldExpanded = expandedState.isExpanded(path);
        final boolean newExpanded = expandedIndices.contains(rowIndex);
        if (newExpanded != oldExpanded) {
          if (newExpanded) {
            expandedState.expand(path);
          } else {
            expandedState.collapse(path);
          }
        }
      }

    }
    data.setRowIndex(-1);
  }

  private static Integer decodeMarkedIndex(FacesContext facesContext, AbstractUIData data) {
    String markedString = null;
    try {
      markedString = (String) facesContext.getExternalContext().getRequestParameterMap()
          .get(data.getClientId(facesContext) + ComponentUtils.SUB_SEPARATOR + AbstractUIData.SUFFIX_MARKED);
      if (org.apache.commons.lang.StringUtils.isBlank(markedString)) {
        return null;
      }
      return Integer.parseInt(markedString);
    } catch (Exception e) {
      // should not happen
      LOG.warn("Can't parse marked: '" + markedString + "'", e);
      return null;
    }
  }

  private static List<Integer> decodeExpandedIndices(FacesContext facesContext, AbstractUIData data) {
    String expandedString = null;
    try {
      expandedString = (String) facesContext.getExternalContext().getRequestParameterMap()
          .get(data.getClientId(facesContext) + ComponentUtils.SUB_SEPARATOR + AbstractUIData.SUFFIX_EXPANDED);
      if (expandedString != null) {
        return StringUtils.parseIntegerList(expandedString);
      }
    } catch (Exception e) {
      // should not happen
      LOG.warn("Can't parse expanded: '" + expandedString + "'", e);
    }
    return null;
  }

  public static String generateUrl(FacesContext facesContext, AbstractUICommandBase component) {

    final Application application = facesContext.getApplication();
    final ViewHandler viewHandler = application.getViewHandler();
    final ExternalContext externalContext = facesContext.getExternalContext();

    String url = null;

    if (component.getResource() != null) {
      boolean jsfResource = component.isJsfResource();
      url = ResourceManagerUtils.getPageWithoutContextPath(facesContext, component.getResource());
      if (url != null) {
        if (jsfResource) {
          url = viewHandler.getActionURL(facesContext, url);
          url = externalContext.encodeActionURL(url);
        } else {
          url = viewHandler.getResourceURL(facesContext, url);
          url = externalContext.encodeResourceURL(url);
        }
      } else {
        url = "";
      }
    } else if (component.getLink() != null) {

      String link = component.getLink();
      if (link.startsWith("/")) { // internal absolute link
        url = viewHandler.getActionURL(facesContext, link);
        url = externalContext.encodeActionURL(url);
      } else if (link.contains(":")) { // external link
        url = link;
      } else { // internal relative link
        url = externalContext.encodeResourceURL(link);
      }

      StringBuilder builder = new StringBuilder(url);
      boolean firstParameter = !url.contains("?");
      for (UIComponent child : (List<UIComponent>) component.getChildren()) {
        if (child instanceof UIParameter) {
          UIParameter parameter = (UIParameter) child;
          if (firstParameter) {
            builder.append("?");
            firstParameter = false;
          } else {
            builder.append("&");
          }
          builder.append(parameter.getName());
          builder.append("=");
          Object value = parameter.getValue();
          // TODO encoding
          builder.append(value != null ? URLDecoder.decode(value.toString()) : null);
        }
      }
      url = builder.toString();
    }

    return url;
  }

}