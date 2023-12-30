package com.yggdrasil.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

class File implements Serializable {
  public int id;
  public String name;

  public File(int id, String name) {
    this.id = id;
    this.name = name;
  }
}

@SpringBootApplication
@RestController
public class CoreApplication {

  List<File> files = new LinkedList<>(List.of(
    new File(1, "file-1.pdf"),
    new File(2, "file-2.pdf"),
    new File(3, "file-3.pdf")
  ));

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
  public File createFile(@RequestBody File file) {
    this.files.add(file);
    return file;
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
}
