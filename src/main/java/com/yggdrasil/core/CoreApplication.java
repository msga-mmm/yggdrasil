package com.yggdrasil.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.Serializable;

@Entity
@Table(name = "files")
class FileEntity {
  @Id
  @GeneratedValue
  public Long id = null;

  @Column(name = "name")
  public String name = null;

  @Column(name = "size")
  public Long size = null;

  @Column(name = "content_type")
  public String contentType = null;

  @Column(name = "user_id")
  public String userID = null;

  @Lob
  @Column(name = "bytes", columnDefinition="BLOB")
  public byte[] bytes;

  public FileEntity() {
  }

  public void setID(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSize(Long size) {
    this.size = size;
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

class FilePojo implements Serializable {
  public Long id = null;
  public String name = null;
  public Long size = null;
  public String contentType = null;
  public String userID = null;

  public void setDataFromFileEntity(FileEntity fileEntity) {
    this.id = fileEntity.id;
    this.name = fileEntity.name;
    this.size = fileEntity.size;
    this.contentType = fileEntity.contentType;
    this.userID = fileEntity.userID;
  }
}

@Repository
interface FileRepository
    extends JpaRepository<FileEntity, Long> {

  public List<FileEntity> findAllByUserID(String userID);

  public Optional<FileEntity> findByIdAndUserID(Long id, String userID);

  public Boolean existsByIdAndUserID(Long id, String userID);
}

@Service
class FileService {

  @Autowired
  private FileRepository fileRepository;

  public FileEntity save(FileEntity fileEntity) {
    return fileRepository.save(fileEntity);
  }

  public List<FileEntity> list() {
    return fileRepository.findAll();
  }

  public Optional<FileEntity> findById(Long id) {
    return fileRepository.findById(id);
  }

  public void delete(FileEntity fileEntity) {
    fileRepository.delete(fileEntity);
  }

  public void deleteById(Long id) {
    fileRepository.deleteById(id);
  }

  public List<FileEntity> findAllByUserID(String userID) {
    return fileRepository.findAllByUserID(userID);
  }

  public Optional<FileEntity> findByIdAndUserID(Long id, String userID) {
    return fileRepository.findByIdAndUserID(id, userID);
  }

  public Boolean existsByIdAndUserID(Long id, String userID) {
    return fileRepository.existsByIdAndUserID(id, userID);
  }
}

@SpringBootApplication
@RestController
public class CoreApplication {
  public static void main(String[] args) {
    SpringApplication.run(CoreApplication.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080", "http://localhost:5173").allowedMethods("*");
      }
    };
  }

  public List<FileEntity> getVisibleUserFiles(String userID) {
    List<FileEntity> visibleUserFiles = fileService.list();
    return visibleUserFiles;
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @Autowired
  private FileService fileService;

  @GetMapping("/files")
  public List<FilePojo> listFiles(@AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    List<FileEntity> visibleUserFiles = fileService.findAllByUserID(userID);
    List<FilePojo> files = visibleUserFiles.stream().map(fileEntity -> {
      FilePojo filePojo = new FilePojo();
      filePojo.setDataFromFileEntity(fileEntity);
      return filePojo;
    }).collect(Collectors.toList());

    return files;
  }

  @PostMapping("/files")
  public ResponseEntity<FilePojo> createFile(@RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal Jwt jwt)
      throws IOException {
    String userID = jwt.getClaimAsString("sub");
    FileEntity fileRecord = new FileEntity();

    fileRecord.setName(file.getOriginalFilename());
    fileRecord.setSize(file.getSize());
    fileRecord.setContentType(file.getContentType());
    fileRecord.setUserID(userID);
    fileRecord.setBytes(file.getBytes());

    fileService.save(fileRecord);

    FilePojo filePojo = new FilePojo();
    filePojo.setDataFromFileEntity(fileRecord);

    return new ResponseEntity<>(filePojo, HttpStatus.CREATED);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Long> deleteFile(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    Optional<FileEntity> fileEntity = fileService.findByIdAndUserID(id, userID);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    fileService.delete(fileEntity.get());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt) throws IOException {
    String userID = jwt.getClaimAsString("sub");
    Optional<FileEntity> fileEntity = fileService.findByIdAndUserID(id, userID);

    if (fileEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    FileEntity fileRecord = fileEntity.get();

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(fileRecord.contentType))
        .body(fileRecord.bytes);
  }
}
