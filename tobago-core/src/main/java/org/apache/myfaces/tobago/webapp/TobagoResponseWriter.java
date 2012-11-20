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

package org.apache.myfaces.tobago.webapp;

import org.apache.myfaces.tobago.config.TobagoConfig;
import org.apache.myfaces.tobago.renderkit.css.Classes;
import org.apache.myfaces.tobago.renderkit.css.Style;
import org.apache.myfaces.tobago.renderkit.html.DataAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.StyleClasses;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This provides an alternative ResponseWriter interfaces, which allows optimizations.
 * E. g. some attributes needed to to be escaped.
 * <p/>
 * Date: 08.05.2007 13:51:43
 */
public abstract class TobagoResponseWriter extends ResponseWriter {

  // same as in ResponseWriter

  @Override
  public abstract void startElement(String name, UIComponent component) throws IOException;

  /**
   * @deprecated Use {@link #startElement(String, UIComponent) startElement(name, null)} instead.
   */
  @Deprecated
  public void startElement(String name) throws IOException {
    startElement(name, null);
  }

  @Override
  public abstract void endElement(String name) throws IOException;
    
  public abstract void write(String string) throws IOException;

  @Override
  public abstract void writeComment(Object comment) throws IOException;

  @Override
  public abstract ResponseWriter cloneWithWriter(Writer writer);

  /**
   * @deprecated Should not directly called via this interface. There is be a special method which might be better.
   */
  @Deprecated
  public abstract void writeAttribute(String name, Object value, final String property) throws IOException;

  /**
   * @deprecated Should not directly called via this interface. There is be a special method which might be better.
   */
  @Deprecated
  public abstract void writeText(Object text, String property) throws IOException;

  @Override
  public abstract void flush() throws IOException;

  // others (not from ResponseWriter)

  /**
   * Writes a string attribute. The renderer may set escape=false to switch of escaping of the string,
   * if it is not necessary.
   */
  public abstract void writeAttribute(String name, String string, boolean escape) throws IOException;

  /**
   * Writes a boolean attribute. The value will not escaped.
   */
  public void writeAttribute(String name, boolean on) throws IOException {
    if (on) {
      writeAttribute(name, name, false);
    }
  }

  /**
   * Writes a integer attribute. The value will not escaped.
   */
  public void writeAttribute(String name, int number) throws IOException {
    writeAttribute(name, Integer.toString(number), false);
  }

  /**
   * Writes a propery as attribute. The value will be escaped.
   */
  public void writeAttributeFromComponent(String name, String property) throws IOException {
    writeAttribute(name, null, property);
  }

  /**
   * Write the id attribute. The value will not escaped.
   */
  public void writeIdAttribute(String id) throws IOException {
    writeAttribute(HtmlAttributes.ID, id, false);
  }

  /**
   * Write the name attribute. The value will not escaped.
   */
  public void writeNameAttribute(String name) throws IOException {
    writeAttribute(HtmlAttributes.NAME, name, false);
  }

  /**
   * Write the class attribute. The value will not escaped.
   * @deprecated since Tobago 1.5.0
   */
  @Deprecated
  public void writeClassAttribute(String cssClass) throws IOException {
    writeAttribute(HtmlAttributes.CLASS, cssClass, false);
  }

  /**
   * Write the class attribute. The value will not escaped.
   * @deprecated since Tobago 1.5.0
   */
  @Deprecated
  public void writeClassAttribute(StyleClasses styleClasses) throws IOException {
    writeAttribute(HtmlAttributes.CLASS, styleClasses.toString(), false);
  }

  /**
   * Write the class attribute. The value will not escaped.
   * <br/>
   * <b>Note:</b> For backward compatibility the value of the Attribute
   * <code>{@link org.apache.myfaces.tobago.component.Attributes#STYLE_CLASS}</code>
   * will be inserted additionally. This function will be removed in later releases.
   * @param classes The abstract representation of the css class string, normally created by the renderer.
   */
  public void writeClassAttribute(Classes classes) throws IOException {
    String styleClasses = getStyleClasses();
    String stringValue = classes.getStringValue();
    if (styleClasses != null) {
      stringValue += " " + styleClasses;
    }
    writeAttribute(HtmlAttributes.CLASS, stringValue, false);
  }

  @Deprecated
  public abstract String getStyleClasses();

  /**
   * Write the class attribute. The value will not escaped.
   * @deprecated since Tobago 1.5.0
   */
  @Deprecated
  public abstract void writeClassAttribute() throws IOException;

/*
  private long self = 0;
  private long gson = 0;

  private synchronized void time(long s, long g) {
    self += s;
    gson += g;
  }

  private static final Logger LOG = LoggerFactory.getLogger(TobagoResponseWriter.class);

  public static final Gson GSON = new Gson();

*/
  /**
   * Write the style attribute. The value will not escaped.
   */
  public void writeStyleAttribute(Style style) throws IOException {
    if (style != null) {
      if (TobagoConfig.getInstance(FacesContext.getCurrentInstance()).getContentSecurityPolicy().size() > 0) {

//        final long a = System.nanoTime();

        final String json = style.encodeJson();

/*
        final long b = System.nanoTime();

        final String json2 = GSON.toJson(style);

        final long c = System.nanoTime();

        time(b-a, c-b);

        LOG.error("self: " + json);
        LOG.error("gson: " + json2);
        LOG.error("self: " + self + " gson: " + gson);

*/
        if (json.length() > 2) { // empty "{}" needs not to be written
          writeAttribute(DataAttributes.STYLE, json, true);
        }
      } else {

        final String value = style.encode();
        if (value.length() > 0) {
          writeAttribute(HtmlAttributes.STYLE, value, style.needsToBeEscaped());
        }
      }
    }
  }

  /**
   * Write the style attribute. The value will not escaped.
   * @deprecated since 1.5.0, use writeStyleAttribute(Style) instead.
   */
  @Deprecated
  public void writeStyleAttribute(String style) throws IOException {
    writeAttribute(HtmlAttributes.STYLE, style, false);
  }

  public void writeJavascript(String script) throws IOException {
    startJavascript();
    write(script);
    endJavascript();
  }

  public void endJavascript() throws IOException {
//    write("\n// -->\n"); // todo: for XHMTL we may need
    endElement(HtmlElements.SCRIPT);
  }

  public void startJavascript() throws IOException {
    startElement(HtmlElements.SCRIPT, null);
    writeAttribute(HtmlAttributes.TYPE, "text/javascript", false);
    flush(); // is needed in some cases, e. g. TOBAGO-1094
//    write("\n<!--\n");
  }

  /**
   * Write text content. The text will be escaped.
   */
  public void writeText(String text) throws IOException {
    writeText(text, null);
  }

  /**
   * Writes a property as text. The text will be escaped.
   */
  public void writeTextFromComponent(String property) throws IOException {
    writeText(null, property);
  }

  public String getContentTypeWithCharSet() {
    String contentType = getContentType();
    if (contentType == null) {
      contentType = "text/html";
    }
    String characterEncoding = getCharacterEncoding();
    if (characterEncoding == null) {
      characterEncoding = "UTF-8";
    }

    StringBuilder builder = new StringBuilder(contentType);
    builder.append(";charset=");
    builder.append(characterEncoding);
    return builder.toString();

  }
}