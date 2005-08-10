package com.atanion.tobago.taglib.decl;

import com.atanion.util.annotation.TagAttribute;
import com.atanion.util.annotation.UIComponentTagAttribute;

/**
 * $Id$
 */
public interface HasLabelWithAccessKey {

  /**
   * Text value to display as label. Overwites 'label'.
   * If text contains an underscore the next character overwrites 'accesskey'.
   */
  @TagAttribute @UIComponentTagAttribute()
      public void setLabelWithAccessKey(String key);


  /**
   * Character used as accesskey. Overwritten by 'labelWithAccessKey'.
   */
//  @TagAttribute @UIComponentTagAttribute(type = String.class)
  @TagAttribute @UIComponentTagAttribute(type={"java.lang.String", "java.lang.Character"})
      public void setAccessKey(String key);
}
