package com.yggdrasil.core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.keycloak.KeycloakPrincipal;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.Iterator;
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

  // @Column(name = "file")
  // public MultipartFile file;

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

  // public FileEntity(
  // Long id,
  // String name,
  // Long size,
  // String contentType,
  // String userID
  // ) {
  // this.id = id;
  // this.name = name;
  // this.size = size;
  // this.contentType = contentType;
  // this.userID = userID;
  // }

  // public FileEntity(Long id, MultipartFile file, String userID) throws
  // IOException {
  // this.id = id;
  // this.userID = userID;

  // // this.file = file;
  // this.name = file.getOriginalFilename();
  // this.size = file.getSize();
  // // this.bytes = file.getBytes();
  // this.contentType = file.getContentType();
  // }

  // public FileEntity(Long id, MultipartFile file, String userID) throws
  // IOException {
  // this.id = id;
  // this.userID = userID;

  // // this.file = file;
  // this.name = file.getOriginalFilename();
  // this.size = file.getSize();
  // // this.bytes = file.getBytes();
  // this.contentType = file.getContentType();
  // }
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

  List<FileEntity> files = new LinkedList<>();

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
    // List<FileEntity> visibleUserFiles = this.files.stream().filter(file ->
    // file.userID.equals(userID))
    // .collect(Collectors.toList());
    List<FileEntity> visibleUserFiles = fileService.list();
    return visibleUserFiles;
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @Autowired
  private FileService fileService;

  // @PersistenceContext
  // private EntityManager em;

  // @Autowired
  // private SessionFactory sessionFactory;

  @GetMapping("/files")
  public List<FilePojo> listFiles(@AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    // List<FileEntity> visibleUserFiles = this.getVisibleUserFiles(userID);
    // List<FileEntity> visibleUserFiles = fileRepository.findAll();
    // List<FileEntity> visibleUserFiles = fileService.list();

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
    // FileEntity fileRecord = new FileEntity((long) this.files.size(), file,
    // userID);
    // FileEntity fileRecord = new FileEntity(
    // (long) this.files.size(),
    // file.getOriginalFilename(),
    // file.getSize(),
    // file.getContentType(),
    // userID);

    FileEntity fileRecord = new FileEntity();

    // fileRecord.setID((long) this.files.size());
    fileRecord.setName(file.getOriginalFilename());
    fileRecord.setSize(file.getSize());
    fileRecord.setContentType(file.getContentType());
    fileRecord.setUserID(userID);
    fileRecord.setBytes(file.getBytes());

    // Session session = sessionFactory.openSession();
    // // Session session = sessionFactory.getCurrentSession();
    // //start transaction
    // session.beginTransaction();
    // //Save the Model object
    // session.save(fileRecord);
    // //Commit transaction
    // session.getTransaction().commit();

    // //terminate session factory, otherwise program won't end
    // session.close();

    fileService.save(fileRecord);
    // this.files.add(fileRecord);

    FilePojo filePojo = new FilePojo();
    filePojo.setDataFromFileEntity(fileRecord);

    return new ResponseEntity<>(filePojo, HttpStatus.CREATED);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Long> deleteFile(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    // Iterator<FileEntity> filesIterator = this.files.iterator();

    // while (filesIterator.hasNext()) {
    // FileEntity file = filesIterator.next();

    // if (file.id == id && file.userID.equals(userID)) {
    // filesIterator.remove();
    // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }
    // }

    // return new ResponseEntity<>(HttpStatus.NOT_FOUND);

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
    // List<FileEntity> visibleUserFiles = this.getVisibleUserFiles(userID);
    // Iterator<FileEntity> filesIterator = visibleUserFiles.iterator();

    // while (filesIterator.hasNext()) {
    // FileEntity file = filesIterator.next();

    // if (file.id == id)
    // return ResponseEntity.ok()
    // .contentType(MediaType.valueOf(file.contentType))
    // .body(file.bytes);
    // }

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
