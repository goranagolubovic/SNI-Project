package com.sni.dms.controllers;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.enums.Action;
import com.sni.dms.requests.*;
import com.sni.dms.responses.FileResponse;
import com.sni.dms.services.FilesService;
import com.sni.dms.services.LogsService;
import com.sni.dms.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@CrossOrigin("*")
public class FilesController {
    private final FilesService filesService;
    private final LogsService logsService;
    private final UserService userService;
    @Value("${PATH}")
    private String path;
    public FilesController(FilesService filesService, LogsService logsService, UserService userService)
    {
        this.filesService = filesService;
        this.logsService = logsService;
        this.userService = userService;
    }

    @PostMapping(value = "/files/all")
    public ResponseEntity<List<FileResponse>> listDirContent(@RequestBody String userDir){
        System.out.println("Try to fetch"+userDir);
        List<FileResponse>files=filesService.getAllFilesFromDefaultDir(userDir);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(value="/files/newDir")
    public ResponseEntity<FileEntity> createNewDir(@RequestBody CreateFileRequest request){
        System.out.println("Creating.................."+request.getFileName());
        FileEntity fileEntity=new FileEntity();
        fileEntity.setIsDir((byte) request.getIsDir());
        fileEntity.setName(request.getRootDir()+"/"+request.getFileName());
        fileEntity.setRootDir(filesService.getIdForDir(request.getRootDir()));
        fileEntity.setIsDeleted((byte)0);
        fileEntity.setUserIdUser(userService.getIdOfUser(request.getUsername()));
        filesService.addNewFile(fileEntity);
        logsService.logAction(Action.CREATE_DIR, fileEntity.getName(),request.getUsername());
        return new ResponseEntity<>(fileEntity, HttpStatus.OK);
    }

    @DeleteMapping(value = "/files/delete")
    public ResponseEntity<FileEntity>deleteFile(@RequestBody DeleteRequest request){
        FileEntity fileEntity=filesService.deleteFile(request.getPath());
        logsService.logAction(Action.DELETE_FILE,request.getPath(),request.getUsername());
        if(fileEntity!=null)
        return ResponseEntity.status(200).body(fileEntity);
        else{
            return ResponseEntity.status(404).body(fileEntity);
        }
    }

    @PostMapping(value = "/files/read")
    public ResponseEntity<byte[]> readFile(@RequestBody DownloadFileRequest request){
        System.out.println("Reading file");
        logsService.logAction(Action.DOWNLOAD_FILE,request.getFilePath(),request.getUsername());
        byte[] file=filesService.readFile(request);
        if(file!=null)
            return ResponseEntity.status(200).body(file);
        else{
            return ResponseEntity.status(403).body(null);
        }
    }

    @PostMapping(value = "/files/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file, @RequestParam String folderName,@RequestParam String username){
        filesService.uploadFile(file,folderName,username);
        logsService.logAction(Action.UPLOAD_FILE,folderName+"/"+file.getOriginalFilename(),username);
            return ResponseEntity.status(200).body("File is uploaded successfully!");
        }

    @PostMapping(value = "/files/edit")
    public ResponseEntity<String> editFile(@RequestBody EditFileRequest request){
        filesService.editFile(request);
        logsService.logAction(Action.EDIT_FILE,request.getFilePath(),request.getUsername());
        return ResponseEntity.status(200).body("File is updated successfully!");
    }

    @PostMapping(value = "/files/parentDir")
    public ResponseEntity<String> findParentDir(@RequestBody  String currentDirJSON){
        System.out.println(currentDirJSON);
        String pathToParent=filesService.getParentDir(currentDirJSON);
        return ResponseEntity.status(200).body(pathToParent);
    }

    @PostMapping(value = "/files/availableDirs")
    public ResponseEntity<List<String>> findAvailableDirs(@RequestBody String availableDirsRequestJSON){
         List<String> availableDirs=filesService.getAvailableDirs(availableDirsRequestJSON);
        return ResponseEntity.status(200).body(availableDirs);
    }
    @PostMapping(value = "/files/sendTo")
    public ResponseEntity<String> moveFile(@RequestBody MoveFileRequest request){
        logsService.logAction(Action.MOVE_FILE,request.getFilePath(),request.getUsername());
        filesService.moveFile(request);
        System.out.println(request.getFilePath());
        return ResponseEntity.status(200).body("Moved");
    }
}
