package org.apache.myfaces.tobago.example.reference;

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

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Upload {

  private static final Logger LOG = LoggerFactory.getLogger(Upload.class);

  private FileItem file;

  private List<UploadItem> list = new ArrayList<UploadItem>();

  public String upload() {
    LOG.info("type=" + file.getContentType());
    LOG.info("file=" + file.get().length);
    LOG.info("name=" + file.getName());
    list.add(new UploadItem(file.getName(), file.get().length, file.getContentType()));
    file = null; // we don't need it in this demo.
    return null;
  }

  public FileItem getFile() {
    return file;
  }

  public void setFile(FileItem file) {
    this.file = file;
  }

  public List<UploadItem> getList() {
    return list;
  }
}