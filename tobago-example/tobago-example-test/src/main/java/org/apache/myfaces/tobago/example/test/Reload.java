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

package org.apache.myfaces.tobago.example.test;

public class Reload {
  
  private int counter;

  public boolean isReload() {
    counter++;
    return true;
  }
  
  public String tick() {
    counter++;
    return null;
  }

  public void reset() {
    counter = 0;
  }

  public boolean isOnlyEven() {
    counter++;
    return counter % 2 == 0;
  }

  public int getCounter() {
    return counter;
  }
}