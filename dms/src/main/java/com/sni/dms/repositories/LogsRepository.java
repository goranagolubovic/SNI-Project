package com.sni.dms.repositories;

import com.sni.dms.entities.LogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<LogsEntity,Integer> {
}
