package com.yggdrasil.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
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

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @GetMapping("/files")
  public List<File> files() {
    List<File> files = new ArrayList<File>();

    files.add(new File(1, "file-1.pdf"));
    files.add(new File(2, "file-2.pdf"));
    files.add(new File(3, "file-2.pdf"));

    return files;
  }
}
