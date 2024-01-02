package com.yggdrasil.core.file;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

  public List<FileModel> getVisibleUserFiles(String userID) {
    List<FileModel> visibleUserFiles = fileService.list();
    return visibleUserFiles;
  }

  @GetMapping("/files")
  public List<FilePojo> listFiles(@AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    List<FileModel> visibleUserFiles = fileService.findAllByUserID(userID);
    List<FilePojo> files = visibleUserFiles.stream().map(fileEntity -> {
      FilePojo filePojo = new FilePojo();
      filePojo.setDataFromFileModel(fileEntity);
      return filePojo;
    }).collect(Collectors.toList());

    return files;
  }

  @PostMapping("/files")
  public ResponseEntity<FilePojo> createFile(@RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal Jwt jwt)
      throws IOException {
    String userID = jwt.getClaimAsString("sub");
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
  public ResponseEntity<Long> deleteFile(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    Optional<FileModel> fileEntity = fileService.findByIdAndUserID(id, userID);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    fileService.delete(fileEntity.get());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    Optional<FileModel> fileEntity = fileService.findByIdAndUserID(id, userID);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    FileModel fileRecord = fileEntity.get();

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(fileRecord.contentType))
        .body(fileRecord.bytes);
  }
}
