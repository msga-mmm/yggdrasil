package com.yggdrasil.core.file;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
  @Autowired
  private FileService fileService;

  @GetMapping("/files")
  public List<FilePojo> listFiles() {
    List<FilePojo> files = fileService.list().stream().map(fileEntity -> {
      FilePojo filePojo = new FilePojo();
      filePojo.setDataFromFileModel(fileEntity);
      return filePojo;
    }).collect(Collectors.toList());

    return files;
  }

  @PostMapping("/files")
  public ResponseEntity<FilePojo> createFile(@RequestParam("file") MultipartFile file)
      throws IOException {
    String userID = fileService.getUserID();
    FileModel fileRecord = new FileModel();

    fileRecord.setName(file.getOriginalFilename());
    fileRecord.setFileSize(file.getSize());
    fileRecord.setContentType(file.getContentType());
    fileRecord.setUserID(userID);
    fileRecord.setBytes(file.getBytes());

    fileService.save(fileRecord);

    FilePojo filePojo = new FilePojo();
    filePojo.setDataFromFileModel(fileRecord);

    return new ResponseEntity<>(filePojo, HttpStatus.CREATED);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Long> deleteFile(@PathVariable Long id) {
    Optional<FileModel> fileEntity = fileService.findById(id);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    fileService.delete(fileEntity.get());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
    Optional<FileModel> fileEntity = fileService.findById(id);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    FileModel fileRecord = fileEntity.get();

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(fileRecord.contentType))
        .body(fileRecord.bytes);
  }
}
