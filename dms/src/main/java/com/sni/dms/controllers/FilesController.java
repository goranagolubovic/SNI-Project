package com.sni.dms.controllers;

import com.sni.dms.entities.FileEntity;
import com.sni.dms.requests.CreateFileRequest;
import com.sni.dms.responses.FileResponse;
import com.sni.dms.services.FilesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
@RestController
@CrossOrigin("*")
public class FilesController {
    private final FilesService filesService;
    @Value("${PATH}")
    private String path;
    public FilesController(FilesService filesService)
    {
        this.filesService = filesService;
    }

    @PostMapping(value = "/files/all")
    public ResponseEntity<List<FileResponse>> listDirContent(@RequestBody String userDir){
        System.out.println("Try to fetch"+userDir);
        List<FileResponse>files=filesService.getAllFilesFromDefaultDir(userDir);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(value="/files/new")
    public ResponseEntity<FileEntity> createNew(@RequestBody CreateFileRequest request){
        System.out.println("Creating.................."+request.getName());
        FileEntity fileEntity=new FileEntity();
        fileEntity.setIsDir((byte) request.getIsDir());
        fileEntity.setName(request.getRootDir()+"/"+request.getName());
        fileEntity.setRootDir(filesService.getIdForDir(request.getRootDir()));
        return new ResponseEntity<>(filesService.addNewFile(fileEntity), HttpStatus.OK);
    }

    @DeleteMapping(value = "/files/delete")
    public ResponseEntity<FileEntity>deleteFile(@RequestBody String body){
        FileEntity fileEntity=filesService.deleteFile(body);
        if(fileEntity!=null)
        return ResponseEntity.status(200).body(fileEntity);
        else{
            return ResponseEntity.status(404).body(fileEntity);
        }
    }

    @PostMapping(value = "/files/read")
    public ResponseEntity<byte[]> readFile(@RequestBody String body){
        System.out.println("Reading file");
        byte[] file=filesService.readFile(body);
        if(file!=null)
            return ResponseEntity.status(200).body(file);
        else{
            return ResponseEntity.status(403).body(null);
        }
    }
}
