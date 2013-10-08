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

package org.apache.myfaces.tobago.apt.generate;

import java.util.TreeSet;
import java.util.Set;


public class Imports {

  private Set<String> imports = new TreeSet<String>();

  private String packageName;

  public Imports() {
  }

  public Imports(String packageName) {
    this.packageName = packageName;
  }

  public void addImport(String qualifiedName) {
    if (!ClassUtils.isSystemClass(qualifiedName) && !ClassUtils.isPrimitive(qualifiedName)) {
      if (!(packageName != null && packageName.equals(ClassUtils.getPackageName(qualifiedName)))) {
        int index = qualifiedName.lastIndexOf('$');
        if (index != -1) {
          qualifiedName = qualifiedName.substring(0, index);
        }
        if (!imports.contains(qualifiedName)) {
          imports.add(qualifiedName);
        }
      }
    }
  }

  public void addImports(Imports newImports) {
    for (String qualifiedName : newImports.imports) {
      addImport(qualifiedName);
    }
  }

  public boolean contains(String qualifiedClassName) {
    return imports.contains(qualifiedClassName);
  }

  public Set<String> getImports() {
    return imports;
  }

}