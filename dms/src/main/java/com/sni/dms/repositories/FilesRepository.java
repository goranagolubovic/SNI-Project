package com.sni.dms.repositories;

import com.sni.dms.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository  extends JpaRepository<FileEntity,Integer> {
}
