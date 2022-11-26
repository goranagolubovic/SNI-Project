package com.sni.dms.services;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.responses.FileResponse;
import org.apache.http.nio.entity.NFileEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
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
}
