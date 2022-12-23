package com.sni.dms.controllers;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.enums.Action;
import com.sni.dms.exceptions.ConflictException;
import com.sni.dms.exceptions.InternalServerError;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.records.ResponseRecord;
import com.sni.dms.requests.*;
import com.sni.dms.responses.FileResponse;
import com.sni.dms.services.FilesService;
import com.sni.dms.services.LogsService;
import com.sni.dms.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
public class FilesController {
    private final FilesService filesService;
    private final LogsService logsService;
    private final UserService userService;

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
        return ResponseEntity.ok(files);
    }

    @PostMapping(value="/files/newDir")
    public ResponseEntity<ResponseRecord> createNewDir(@RequestBody CreateFileRequest request){
        try {
            System.out.println("Creating.................." + request.getFileName());
            FileEntity fileEntity = new FileEntity();
            fileEntity.setIsDir((byte) request.getIsDir());
            fileEntity.setName(request.getRootDir() + "/" + request.getFileName());
            fileEntity.setRootDir(filesService.getIdForDir(request.getRootDir()));
            fileEntity.setIsDeleted((byte) 0);
            fileEntity.setUserIdUser(userService.getIdOfUser(request.getUsername()));
            filesService.addNewFile(fileEntity);
            logsService.logAction(Action.CREATE_DIR, fileEntity.getName(), request.getUsername());
            return  ResponseEntity.ok(new ResponseRecord(200,""));
        }
        catch (NotFoundException e1){
            return ResponseEntity.ok(new ResponseRecord(404,e1.getMessage()));
        }
        catch (ConflictException e2){
            return ResponseEntity.ok(new ResponseRecord(409,e2.getMessage()));
        }
    }

    @DeleteMapping(value = "/files/delete")
    public ResponseEntity<ResponseRecord>deleteFile(@RequestBody DeleteRequest request){
        try {
            filesService.deleteFile(request.getPath());
            logsService.logAction(Action.DELETE_FILE, request.getPath(), request.getUsername());
            return ResponseEntity.ok(new ResponseRecord(200,""));
        }
        catch (NotFoundException exception){
            return ResponseEntity.ok(new ResponseRecord(404,exception.getMessage()));
        }
    }

    @PostMapping(value = "/files/read")
    public ResponseEntity<byte[]> readFile(@RequestBody DownloadFileRequest request){
        byte [] file = new byte[0];
        try {
            if ("download".equals(request.getAction())) {
                logsService.logAction(Action.DOWNLOAD_FILE, request.getFilePath(), request.getUsername());
            }
            file = filesService.readFile(request);
            return ResponseEntity.ok(file);
        }
        catch (NotFoundException | InternalServerError exception){
            return ResponseEntity.ok(file);
        }
    }

    @PostMapping(value = "/files/upload")
    public ResponseEntity<ResponseRecord> uploadFile(@RequestParam MultipartFile file, @RequestParam String folderName,@RequestParam String username){
        try {
            filesService.uploadFile(file, folderName, username);
            logsService.logAction(Action.UPLOAD_FILE, folderName + "/" + file.getOriginalFilename(), username);
            return ResponseEntity.ok(new ResponseRecord(200,"File is uploaded successfully"));
        }
        catch (NotFoundException exception){
            return ResponseEntity.ok(new ResponseRecord(404,exception.getMessage()));
        } catch (InternalServerError e1) {
            return  ResponseEntity.ok(new ResponseRecord(500,e1.getMessage()));
        } catch (ConflictException e2) {
            return ResponseEntity.ok(new ResponseRecord(409, e2.getMessage()));
        }
    }

    @PostMapping(value = "/files/edit")
    public ResponseEntity<ResponseRecord> editFile(@RequestBody EditFileRequest request){
        try {
            filesService.editFile(request);
            logsService.logAction(Action.EDIT_FILE, request.getFilePath(), request.getUsername());
            return ResponseEntity.ok(new ResponseRecord(200,"File is updated successfully"));
        }catch (NotFoundException exception){
            return ResponseEntity.ok(new ResponseRecord(404,exception.getMessage()));
        } catch (InternalServerError ex) {
            return ResponseEntity.ok(new ResponseRecord(500,ex.getMessage()));
        }
    }

    @PostMapping(value = "/files/parentDir")
    public ResponseEntity<ResponseRecord> findParentDir(@RequestBody  String currentDirJSON){
        System.out.println(currentDirJSON);
        try {
            String pathToParent = filesService.getParentDir(currentDirJSON);
            return ResponseEntity.ok(new ResponseRecord(200,pathToParent));
        }
        catch (NotFoundException exception){
            return ResponseEntity.ok(new ResponseRecord(404,exception.getMessage()));
        }
    }

    @PostMapping(value = "/files/availableDirs")
    public ResponseEntity<List<String>> findAvailableDirs(@RequestBody String availableDirsRequestJSON){
         List<String> availableDirs=filesService.getAvailableDirs(availableDirsRequestJSON);
        return ResponseEntity.status(200).body(availableDirs);
    }
    @PostMapping(value = "/files/sendTo")
    public ResponseEntity<ResponseRecord> moveFile(@RequestBody MoveFileRequest request){
        try {
            logsService.logAction(Action.MOVE_FILE,request.getFilePath(),request.getUsername());
            filesService.moveFile(request);
            System.out.println(request.getFilePath());
            return ResponseEntity.ok(new ResponseRecord(200,""));
        } catch (NotFoundException e1) {
            return ResponseEntity.ok(new ResponseRecord(404,e1.getMessage()));
        } catch (InternalServerError e2) {
            return ResponseEntity.ok(new ResponseRecord(500,e2.getMessage()));
        }

    }
}
