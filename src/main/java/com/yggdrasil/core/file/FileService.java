package com.yggdrasil.core.file;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class FileService {
  @Autowired
  private FileRepository fileRepository;

  public FileModel save(FileModel fileEntity) {
    return fileRepository.save(fileEntity);
  }

  public Jwt getJwt() {
    Jwt jwt = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      jwt = ((Jwt) authentication.getPrincipal());
    }

    return jwt;
  }

  public String getUserID() {
    return getJwt().getClaimAsString("sub");
  }

  public List<FileModel> list() {
    String userID = getUserID();
    return fileRepository.findAllByUserID(userID);
  }

  public Optional<FileModel> findById(Long id) {
    String userID = getUserID();
    return fileRepository.findByIdAndUserID(id, userID);
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
