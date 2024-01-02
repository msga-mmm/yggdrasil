package com.yggdrasil.core.file;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
  @Autowired
  private FileRepository fileRepository;

  public FileModel save(FileModel fileEntity) {
    return fileRepository.save(fileEntity);
  }

  public List<FileModel> list() {
    return fileRepository.findAll();
  }

  public Optional<FileModel> findById(Long id) {
    return fileRepository.findById(id);
  }

  public void delete(FileModel fileEntity) {
    fileRepository.delete(fileEntity);
  }

  public void deleteById(Long id) {
    fileRepository.deleteById(id);
  }

  public List<FileModel> findAllByUserID(String userID) {
    return fileRepository.findAllByUserID(userID);
  }

  public Optional<FileModel> findByIdAndUserID(Long id, String userID) {
    return fileRepository.findByIdAndUserID(id, userID);
  }

  public Boolean existsByIdAndUserID(Long id, String userID) {
    return fileRepository.existsByIdAndUserID(id, userID);
  }
}
