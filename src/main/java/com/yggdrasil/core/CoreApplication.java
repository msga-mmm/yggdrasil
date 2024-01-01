package com.yggdrasil.core;

import org.keycloak.KeycloakPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.io.Serializable;

class File implements Serializable {
  public int id;
  public String name;
  public Long size;
  public String contentType;
  public String userID;

  transient MultipartFile file;
  transient byte[] bytes;

  public File(int id, MultipartFile file, String userID) throws IOException {
    this.id = id;
    this.userID = userID;

    this.file = file;
    this.name = file.getOriginalFilename();
    this.size = file.getSize();
    this.bytes = file.getBytes();
    this.contentType = file.getContentType();
  }
}

@SpringBootApplication
@RestController
public class CoreApplication {

  List<File> files = new LinkedList<>();

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

  public List<File> getVisibleUserFiles(String userID) {
    List<File> visibleUserFiles = this.files.stream().filter(file -> file.userID.equals(userID))
        .collect(Collectors.toList());
    return visibleUserFiles;
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @GetMapping("/files")
  public List<File> listFiles(@AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    List<File> visibleUserFiles = this.getVisibleUserFiles(userID);

    return visibleUserFiles;
  }

  @PostMapping("/files")
  public ResponseEntity<File> createFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt)
      throws IOException {
    String userID = jwt.getClaimAsString("sub");
    File fileRecord = new File(this.files.size(), file, userID);

    this.files.add(fileRecord);

    return new ResponseEntity<File>(fileRecord, HttpStatus.CREATED);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Long> deleteFile(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    String userID = jwt.getClaimAsString("sub");
    Iterator<File> filesIterator = this.files.iterator();

    while (filesIterator.hasNext()) {
      File file = filesIterator.next();

      if (file.id == id && file.userID.equals(userID)) {
        filesIterator.remove();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) throws IOException {
    String userID = jwt.getClaimAsString("sub");
    List<File> visibleUserFiles = this.getVisibleUserFiles(userID);
    Iterator<File> filesIterator = visibleUserFiles.iterator();

    while (filesIterator.hasNext()) {
      File file = filesIterator.next();

      if (file.id == id)
        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(file.contentType))
            .body(file.bytes);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
