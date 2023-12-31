package com.yggdrasil.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.io.Serializable;

class File implements Serializable {
  public int id;
  public String name;
  public Long size;
  public String contentType;

  transient MultipartFile file;
  transient byte[] bytes;

  public File(int id, MultipartFile file) throws IOException {
    this.id = id;

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

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @GetMapping("/files")
  public List<File> listFiles() {
    return this.files;
  }

  @PostMapping("/files")
  public ResponseEntity<Boolean> createFile(@RequestParam("file") MultipartFile file) throws IOException {
    File fileRecord = new File(this.files.size(), file);
    this.files.add(fileRecord);
    return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Long> deleteFile(@PathVariable Long id) {
    Iterator<File> filesIterator = this.files.iterator();

    while (filesIterator.hasNext()) {
        File file = filesIterator.next();

        if (file.id == id)
            filesIterator.remove();
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws IOException {
    Iterator<File> filesIterator = this.files.iterator();

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
