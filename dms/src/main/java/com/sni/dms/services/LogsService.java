package com.sni.dms.services;

import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.LogsEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.enums.Action;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.repositories.LogsRepository;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.responses.LogsResponse;
import org.apache.tomcat.jni.User;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LogsService {
    private final LogsRepository logsRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;
    public LogsService(LogsRepository logsRepository, UserRepository userRepository, FilesRepository filesRepository){
        this.logsRepository=logsRepository;
        this.userRepository=userRepository;
        this.filesRepository=filesRepository;
    }
    public void logAction(Action action, String fileName, String username){
        LogsEntity logEntity = new LogsEntity();
        logEntity.setAction(String.valueOf(action));
        logEntity.setFileIdfile(getIdOfFile(fileName));
        logEntity.setUserIdUser(getIdOfUser(username));
        logEntity.setDateTime(Timestamp.valueOf(LocalDateTime.now()));
        logsRepository.save(logEntity);
    }

    public List<LogsResponse> readAllLogs(){
        return logsRepository.findAll().stream().map(elem->new LogsResponse(userRepository.findById(elem.getUserIdUser()).get().getUsername(),
                filesRepository.findById(elem.getFileIdfile()).get().getName(),elem.getAction(),elem.getDateTime())).collect(Collectors.toList());
    }

    private int getIdOfFile(String fileName){
        Optional<FileEntity> fileEntity= filesRepository.findAll().stream()
                .filter(elem->elem.getName().equals(fileName)).findAny();
        if(fileEntity.isPresent()){
            return fileEntity.get().getIdfile();
        }
        else return -1;
    }

    private int getIdOfUser(String username){
        Optional<UserEntity> userEntity= userRepository.findAll().stream()
                .filter(elem->elem.getUsername().equals(username)).findAny();
        if(userEntity.isPresent()){
            return userEntity.get().getIdUser();
        }
        else return -1;
    }
}
