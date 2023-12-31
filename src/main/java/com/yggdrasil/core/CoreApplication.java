package com.yggdrasil.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

class File implements Serializable {
  public int id;
  public String name;
  public Long size;

  // @JsonSerialize(using = FileFileSerializer.class)
  transient MultipartFile file;
  transient byte[] bytes;

  public File(int id, MultipartFile file) throws IOException {
    this.id = id;

    this.file = file;
    this.name = file.getOriginalFilename();
    this.size = file.getSize();
    this.bytes= file.getBytes();
  }
}

class FileFileSerializer extends JsonSerializer<MultipartFile> {
    @Override
    public void serialize(MultipartFile file, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(file.getOriginalFilename());
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
  public @ResponseBody byte[] downloadFile(@PathVariable Long id) throws IOException {
    Iterator<File> filesIterator = this.files.iterator();

    while (filesIterator.hasNext()) {
        File file = filesIterator.next();

        if (file.id == id)
            return file.bytes;
    }

    throw new IOException();
  }
}
