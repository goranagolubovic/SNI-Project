package com.sni.dms.services;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.requests.*;
import com.sni.dms.responses.FileResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.http.nio.entity.NFileEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
    @Value("${regex}")
    public String MATCH_ALL_REGEX;

    public FilesService(FilesRepository filesRepository){
        this.filesRepository=filesRepository;
    }
    //getAllFiles in default dir
    public List<FileResponse> getAllFilesFromDefaultDir(String defaultDirJson){
        String defaultDir=new Gson().fromJson(defaultDirJson,String.class);
        File defaultFile=new File(defaultDir);
        return filesRepository.findAll().stream().filter(elem->new File(elem.getName()).getParent().equals(defaultFile.getPath()))
                .map(elem->new FileResponse(elem.getIdfile(),elem.getName().split("/")[elem.getName().split("/").length-1], elem.getIsDir(), elem.getRootDir()))
                .collect(Collectors.toList());
    }

    public FileEntity addNewFile(FileEntity fileEntity){
        File f=new File(fileEntity.getName());
        if(fileEntity.getIsDir()==1){
            f.mkdirs();
        }
        else {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return filesRepository.save(fileEntity);
    }
    private String getDirNameForId(int id){
        Optional<FileEntity> file= filesRepository.findAll().stream().filter(elem->elem.getIdfile()==id).findAny();
        if(file.isPresent()){
            return file.get().getName();
        }
        return "";
    }
    public Integer getIdForDir(String rootDir) {
       Optional<FileEntity>opt= filesRepository.findAll().stream()
                .filter(elem->elem.getName().equals(rootDir)).findAny();
        //ako postoji samo jedan fajl sa takvim imenom na cijelom fajl sistemu
        if(opt.isPresent()){
            return opt.get().getIdfile();
        }
        return -1;
    }

    public FileEntity deleteFile(String pathJson){
        String path=new Gson().fromJson(pathJson,String.class);
        File file=new File(path);
        System.out.println(file.toPath());
        if(file.exists()){
            try {

                Files.delete(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileEntity fileEntity=findFileEntityFor(path);
        if(fileEntity!=null)
        filesRepository.delete(fileEntity);
        return fileEntity;
    }

    private FileEntity findFileEntityFor(String path) {
        System.out.println("---TRY OF DELETE---");
        Optional<FileEntity>opt= filesRepository.findAll().stream().filter(elem->
            elem.getName().equals(path)
        ).findAny();
//        filesRepository.findAll().stream().forEach(elem->
//                System.out.println(elem.getName()+" "+path)
//        );
       return (opt.isPresent() ?  opt.get() :  null);
    }

    @SneakyThrows
    public byte[] readFile(String downloadFileRequestJson) {
        DownloadFileRequest downloadFileRequest=new Gson().fromJson(downloadFileRequestJson,DownloadFileRequest.class);
        String filePath=downloadFileRequest.getFilePath();
        String userDir=downloadFileRequest.getUserDir();
        System.out.println("File is"+filePath);
        File file=new File(filePath);
        byte[] bytes={};
//        if(checkIfFileIsInUserDir(userDir,file)) {
            bytes = Files.readAllBytes(file.toPath());
            return bytes;
//        }
//        return null;
    }

    @SneakyThrows
    public void uploadFile(MultipartFile file,String folderPath) {
        File folder=new File(folderPath);
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(folder.getAbsolutePath()+File.separator+file.getOriginalFilename()));

        FileEntity fileEntity=new FileEntity();
        fileEntity.setIsDir((byte) 0);
        fileEntity.setName(folderPath+"/"+file.getOriginalFilename());
        //mozda bude bespotrebno ovo
        fileEntity.setRootDir(getIdForDir(folderPath));
        filesRepository.save(fileEntity);
    }

    @SneakyThrows
    public void editFile(String editFileRequestJson) {
        System.out.println(editFileRequestJson);
        EditFileRequest editFileRequest=new Gson().fromJson(editFileRequestJson, EditFileRequest.class);
        File fileOld=new File(editFileRequest.getFilePath());
        fileOld.delete();
        File fileNew=new File(editFileRequest.getFilePath());

            FileWriter fileWriter = new FileWriter(fileNew, false);
            fileWriter.write(editFileRequest.getFileContent());
            fileWriter.close();
    }

    public String getParentDir(String currentDirJSON) {
        System.out.println(currentDirJSON);
        String currentDir=new Gson().fromJson(currentDirJSON,String.class);
        int index= filesRepository.findAll().stream().filter(elem->elem.getName().equals(currentDir)).findAny().get().getRootDir();
        return  filesRepository.findById(index).get().getName();
    }

    public List<String> getAvailableDirs(String availableDirsRequestJSON) {
        AvailableDirsRequest availableDirsRequest=new Gson().fromJson(availableDirsRequestJSON,AvailableDirsRequest.class);
        String currentDir=availableDirsRequest.getCurrentDir();
        System.out.println("Now in"+currentDir);
        String userDir = availableDirsRequest.getUserDir();
        List<FileEntity> availableDirs = filesRepository.findAll().stream().filter(elem -> !(elem.getName().equals(currentDir)) && elem.getIsDir() == 1 && elem.getName().startsWith(userDir)).collect(Collectors.toList());
        return availableDirs.stream().map(elem -> elem.getName()).collect(Collectors.toList());
    }

    @SneakyThrows
    public void moveFile(String data) {
        MoveFileRequest moveFileRequest=new Gson().fromJson(data,MoveFileRequest.class);
        String destinationDirPath=moveFileRequest.getDestinationDir();
        String filePath = moveFileRequest.getFilePath();
        String fileName=moveFileRequest.getFileName();
        File newFile=new File(destinationDirPath+"/"+fileName);
        newFile.createNewFile();
        File oldFile=new File(filePath);
        FileUtils.copyFile(oldFile, newFile);
        oldFile.delete();


        FileEntity fileEntity=filesRepository.findAll().stream().filter(elem->elem.getName().equals(filePath)).findAny().get();
        fileEntity.setName(destinationDirPath+"/"+fileName);
        fileEntity.setRootDir(getIdForDir(destinationDirPath));
        filesRepository.save(fileEntity);

    }


//    private boolean checkIfFileIsInUserDir(String userDir, File file)
//    {
//        File rootDir=new File(userDir);
//        return isFileAChildOf(rootDir,file);
//    }

//    private boolean isFileAChildOf(File rootDir, File file)
//    {
//        while(file!=null){
//            if(!file.getParent().equals(rootDir)){
//               file=new File(file.getParent());
//            }
//            else{
//                return  true;
//            }
//        }
//        return false;
//    }
}
