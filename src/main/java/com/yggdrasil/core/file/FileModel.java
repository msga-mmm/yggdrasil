package com.yggdrasil.core.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "files")
class FileModel {
  @Id
  @GeneratedValue
  public Long id = null;

  @Column(name = "name")
  public String name = null;

  @Column(name = "file_size")
  public Long fileSize = null;

  @Column(name = "content_type")
  public String contentType = null;

  @Column(name = "user_id")
  public String userID = null;

  @Column(name = "bytes", columnDefinition = "bytea")
  public byte[] bytes;

  public FileModel() {}

  public void setID(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFileSize(Long fileSise) {
    this.fileSize = fileSise;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
}
