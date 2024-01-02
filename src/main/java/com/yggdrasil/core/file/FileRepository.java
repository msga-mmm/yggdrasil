package com.yggdrasil.core.file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FileRepository
    extends JpaRepository<FileModel, Long> {

  public List<FileModel> findAllByUserID(String userID);

  public Optional<FileModel> findByIdAndUserID(Long id, String userID);

  public Boolean existsByIdAndUserID(Long id, String userID);
}
