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

function getMenubarBorderWidth() {
  return 1;  
}

function getSubitemContainerBorderWidthSum() {
  return 2; // border * 2
}

function getItemHeight(menu) {
  if (menu && menu.level == 1) {
    if (menu.parent.popup) {
      if (menu.parent.popup == "ToolBarButton") {
        return 18;
      }
      else if (menu.parent.popup == "SheetSelector") {
       return 16;
      }
    }

    if (Tobago.element(menu.parent.menubarId).className.match(/tobago-menuBar-page-facet/)) {
      return 20;
    }
    else {
      return 18;
    }
  }
  else {
    return 18;
  }
}

function getSeparatorHeight() {
  return 8;
}

function getPopupMenuWidth() {
  return 16;
}

function getMenuArrowWidth() {
  return 16;
}

function getPopupImageTop(popup) {
  if (popup == "ToolBarButton") {
    return "2px";
  }
  else if (popup == "SheetSelector") {
    return "0px";
  }
  else {
    LOG.debug("unknown popup type: " + popup);
    return "0px";
  }
}