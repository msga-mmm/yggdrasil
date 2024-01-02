package com.yggdrasil.core.file;

import java.io.Serializable;

class FilePojo implements Serializable {
  public Long id = null;
  public String name = null;
  public Long size = null;
  public String contentType = null;
  public String userID = null;

  public void setDataFromFileModel(FileModel fileEntity) {
    this.id = fileEntity.id;
    this.name = fileEntity.name;
    this.size = fileEntity.size;
    this.contentType = fileEntity.contentType;
    this.userID = fileEntity.userID;
  }
}
