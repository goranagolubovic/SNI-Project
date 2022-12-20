package com.sni.dms.services;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.exceptions.ConflictException;
import com.sni.dms.exceptions.InternalServerError;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.*;
import com.sni.dms.responses.FileResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
    private final UserRepository userRepository;
    @Value("${regex}")
    public String MATCH_ALL_REGEX;

    public FilesService(FilesRepository filesRepository,UserRepository userRepository){
        this.filesRepository=filesRepository;
        this.userRepository=userRepository;
    }
    //getAllFiles in default dir
    public List<FileResponse> getAllFilesFromDefaultDir(String defaultDirJson){
        String defaultDir=new Gson().fromJson(defaultDirJson,String.class);
        File defaultFile=new File(defaultDir);
        return filesRepository.findAll().stream().filter(elem->elem.getIsDeleted()==0 && new File(elem.getName()).getParent().equals(defaultFile.getPath()))
                .map(elem->new FileResponse(elem.getIdfile(),elem.getName().split("/")[elem.getName().split("/").length-1], elem.getIsDir(), elem.getRootDir()))
                .collect(Collectors.toList());
    }

    public FileEntity addNewFile(FileEntity fileEntity) throws ConflictException{
        checkIfTheSameFileExists(fileEntity);
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

    private void checkIfTheSameFileExists(FileEntity fileEntity) throws ConflictException {
        boolean exists=filesRepository.findAll().stream()
                .anyMatch(elem->elem.getUserIdUser()==fileEntity.getUserIdUser() && elem.getName().equals(fileEntity.getName()));
        if(exists){
            throw  new ConflictException("There is file with same name in the folder.");
        }
    }

    private String getDirNameForId(int id){
        Optional<FileEntity> file= filesRepository.findAll().stream().filter(elem->elem.getIdfile()==id).findAny();
        if(file.isPresent()){
            return file.get().getName();
        }
        return "";
    }
    public Integer getIdForDir(String rootDir) throws NotFoundException{
       Optional<FileEntity>opt= filesRepository.findAll().stream()
                .filter(elem->elem.getName().equals(rootDir)).findAny();
        //ako postoji samo jedan fajl sa takvim imenom na cijelom fajl sistemu
        if(!opt.isPresent()){
          throw new NotFoundException("File is not found.");
        }
        return opt.get().getIdfile();
    }

    public void deleteFile(String path) throws NotFoundException{
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
        if(fileEntity!=null) {
            fileEntity.setIsDeleted((byte) 1);
            filesRepository.save(fileEntity);
        }
    }

    private FileEntity findFileEntityFor(String path)throws NotFoundException{
        Optional<FileEntity>opt= filesRepository.findAll().stream().filter(elem->
            elem.getName().equals(path)
        ).findAny();
//        filesRepository.findAll().stream().forEach(elem->
//                System.out.println(elem.getName()+" "+path)
//        );
        if(opt.isEmpty()){
            throw new NotFoundException("File is not found.");
        }
       return opt.get();
    }

    public byte[] readFile(DownloadFileRequest downloadFileRequest) throws InternalServerError {
        String filePath=downloadFileRequest.getFilePath();
        String userDir=downloadFileRequest.getUserDir();
        System.out.println("File is"+filePath);
        File file=new File(filePath);
        byte[] bytes={};
//        if(checkIfFileIsInUserDir(userDir,file)) {
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new InternalServerError("Error in I/O file operation");
        }
        return bytes;
//        }
//        return null;
    }


    public void uploadFile(MultipartFile file,String folderPath,String username) throws NotFoundException, InternalServerError {
        File folder=new File(folderPath);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(folder.getAbsolutePath()+File.separator+file.getOriginalFilename()));
        } catch (IOException e) {
            throw new InternalServerError("Error in I/O file operation");
        }

        FileEntity fileEntity=new FileEntity();
        fileEntity.setIsDir((byte) 0);
        fileEntity.setIsDeleted((byte)0);
        fileEntity.setName(folderPath+"/"+file.getOriginalFilename());
        //mozda bude bespotrebno ovo
        fileEntity.setRootDir(getIdForDir(folderPath));
        fileEntity.setUserIdUser(getIdForUser(username));
        filesRepository.save(fileEntity);
    }

    private int getIdForUser(String username) throws NotFoundException{
        Optional<UserEntity>opt= userRepository.findAll().stream()
                .filter(elem->elem.getUsername().equals(username)).findAny();
        if(!opt.isPresent()){
           throw new NotFoundException("User is not found");
        }
        return opt.get().getIdUser();
    }


    public void editFile(EditFileRequest editFileRequest) throws InternalServerError {
        File fileOld=new File(editFileRequest.getFilePath());
        fileOld.delete();
        File fileNew=new File(editFileRequest.getFilePath());

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileNew, false);
            fileWriter.write(editFileRequest.getFileContent());
            fileWriter.close();
        } catch (IOException e) {
            throw new InternalServerError("Error in I/O file operation");
        }

    }

    public String getParentDir(String currentDirJSON) throws  NotFoundException{
        String currentDir=new Gson().fromJson(currentDirJSON,String.class);
        Optional<FileEntity>opt= filesRepository.findAll().stream().filter(elem->elem.getName().equals(currentDir)).findAny();
        if(opt.isEmpty()){
            throw  new NotFoundException("File is not found");
        }
        int index=opt.get().getRootDir();
        return  filesRepository.findById(index).get().getName();
    }

    public List<String> getAvailableDirs(String availableDirsRequestJSON) {
        AvailableDirsRequest availableDirsRequest=new Gson().fromJson(availableDirsRequestJSON,AvailableDirsRequest.class);
        String currentDir=availableDirsRequest.getCurrentDir();
        System.out.println("Now in"+currentDir);
        String userDir = availableDirsRequest.getUserDir();
        List<FileEntity> availableDirs = filesRepository.findAll().stream().filter(elem ->elem.getIsDeleted()==0 && !(elem.getName().equals(currentDir)) && elem.getIsDir() == 1 && elem.getName().startsWith(userDir)).collect(Collectors.toList());
        return availableDirs.stream().map(elem -> elem.getName()).collect(Collectors.toList());
    }


    public void moveFile(MoveFileRequest moveFileRequest) throws NotFoundException, InternalServerError {
        String destinationDirPath=moveFileRequest.getDestinationDir();
        String filePath = moveFileRequest.getFilePath();
        String fileName=moveFileRequest.getFileName();
        File newFile=new File(destinationDirPath+"/"+fileName);
        File oldFile=new File(filePath);
        try {
            newFile.createNewFile();
            FileUtils.copyFile(oldFile, newFile);
        } catch (IOException e) {
            throw new InternalServerError("Error in I/O file operation.");
        }
        oldFile.delete();

        Optional<FileEntity>opt=filesRepository.findAll().stream().filter(elem->elem.getName().equals(filePath)).findAny();
        if(opt.isEmpty()){
            throw  new NotFoundException("File is not found");
        }
        FileEntity fileEntity=opt.get();
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
