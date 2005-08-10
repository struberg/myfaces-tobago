package com.atanion.tobago.taglib.decl;

import com.atanion.util.annotation.TagAttribute;
import com.atanion.util.annotation.UIComponentTagAttribute;

/**
 * $Id$
 */
public interface HasBorder {
  /**
   *
   *  Border size of this component.
   *
   */
  @TagAttribute @UIComponentTagAttribute()
  public void setBorder(String border);
}
