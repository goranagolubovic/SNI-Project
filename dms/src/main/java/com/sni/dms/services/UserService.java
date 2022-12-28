package com.sni.dms.services;

import com.sni.dms.configuration.KeycloakProvider;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.exceptions.ConflictException;
import com.sni.dms.exceptions.ForbiddenAccessFromIpAddress;
import com.sni.dms.exceptions.InternalServerError;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.ChangePasswordRequest;
import com.sni.dms.service.KeycloakAdminClientService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    private UserRepository userRepository;
    private FilesRepository filesRepository;
    private final KeycloakProvider kcProvider;



    private Path root;

    public UserService(UserRepository userRepository, FilesRepository filesRepository, KeycloakProvider kcProvider, @Value("${PATH}") String path){
        this.userRepository = userRepository;
        this.filesRepository=filesRepository;
        this.kcProvider=kcProvider;
        this.root = Paths.get(path);
    }
    public static UsersResource getUsersFromKeyCloak(){
        Keycloak keycloak=KeycloakProvider.getInstance();
        return keycloak.realm("SNI").users();
    }
    public static UserRepresentation getKeyCloakUser(String username){
        Optional<org.keycloak.representations.idm.UserRepresentation> user = getUsersFromKeyCloak().list().stream().filter(elem->elem.getUsername().equals(username)).findAny();
        if(user.isPresent())
            return  user.get();
        else
            return null;
    }

    public static List<RoleRepresentation> getRealmRole(String role) {
        Keycloak keycloak=KeycloakProvider.getInstance();
        List<RoleRepresentation>roleList=new ArrayList<>();
        roleList.add(keycloak.realm("SNI").roles().get(role).toRepresentation());
        return roleList;
    }

    public void delete(String username) throws NotFoundException, InternalServerError {
        System.out.println(username);
       UserEntity user=userRepository.findAll().stream().filter(elem->elem.getUsername().equals(username)
       && elem.getIsDeleted()==0).findAny().get();
//        Optional<UserEntity> user = userRepository.findAll().stream()
//                .filter(elem -> elem.getUsername().equals(username)).findAny();
            Optional<FileEntity> fileEntity=filesRepository.findAll().stream().filter(elem->elem.getName().equals(user.getUserDir())).findAny();
            if(fileEntity.isEmpty()){
                throw new NotFoundException("User dir is not found");
            }
            fileEntity.ifPresent(f->{
                f.setIsDeleted((byte) 1);
                //prodji kroz sve fajlove i one koje je kreirao ovaj korisnik setuj na obrisano
                filesRepository.findAll().stream().forEach(elem-> {
                    if (elem.getRootDir()!=null && elem.getUserIdUser() == user.getIdUser()){
                        elem.setIsDeleted((byte) 1);
                        filesRepository.save(elem);

                }
                });
                filesRepository.save(f);
            });
            user.setIsDeleted((byte)1);
            userRepository.save(user);
            getUsersFromKeyCloak().get(getKeyCloakUser(username).getId()).remove();
    removeDefaultDirFromFileSystem(Path.of(user.getUserDir()));

    }

    private void removeDefaultDirFromFileSystem(Path pathToDir) throws InternalServerError {
        try {
            Files.walk(pathToDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new InternalServerError("Erro in I/O file operation.");
        }
    }

    public void updateUser(UserEntity user,String ip) throws NotFoundException, ForbiddenAccessFromIpAddress {

        UserEntity e = getUser(user.getUsername(),ip);
        if (e != null) {
            e.setUsername(user.getUsername());
            e.setRole(user.getRole());
            e.setIpAddress(user.getIpAddress());
            e.setUserDir(user.getUserDir());
            e.setIsCreateApproved(user.getIsCreateApproved());
            e.setIsReadApproved(user.getIsReadApproved());
            e.setIsUpdateApproved(user.getIsUpdateApproved());
            e.setIsDeleteApproved(user.getIsDeleteApproved());
            e.setIsPasswordChanged(user.getIsPasswordChanged());
            userRepository.save(e);
        }
       else{
           throw new NotFoundException("User is not found");
        }

    }

    public UserEntity getUser(String username,String ip) throws NotFoundException, ForbiddenAccessFromIpAddress {
        Optional<UserEntity> user = userRepository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(username) && elem.getIsDeleted()==0).findAny();
        System.out.println("Ussername"+username);
        if(user.isEmpty()){
            throw  new NotFoundException("User is not found.");
        }
        UserEntity u=user.get();
        if(u.getRole().equals("client") && u.getIpAddress()!=null && !"".equals(u.getIpAddress()) && !u.getIpAddress().equals(ip)){
            throw new ForbiddenAccessFromIpAddress("Cannot access app from this ip adress.");
        }
        return u;
    }

    public void createDefaultDirForUser(String userDir) {
        System.out.println(userDir);
        File file=new File(userDir);
        file.mkdirs();
    }

    public static void assignCRUDPrivilegis(UserEntity user) {
        UserRepresentation userRepresentation=getKeyCloakUser(user.getUsername());
        UsersResource usersResource=getUsersFromKeyCloak();
        UserResource userResource = usersResource.get(userRepresentation.getId());
        if(user.getIsCreateApproved()!=null && user.getIsCreateApproved()==1){
            userResource.roles().realmLevel().add(getRealmRole("create"));
        }
        if(user.getIsReadApproved()!=null && user.getIsReadApproved()==1){
            userResource.roles().realmLevel().add(getRealmRole("read"));
        }
        if(user.getIsUpdateApproved()!=null && user.getIsUpdateApproved()==1){
            userResource.roles().realmLevel().add(getRealmRole("update"));
        }
        if(user.getIsDeleteApproved()!=null && user.getIsDeleteApproved()==1){
            userResource.roles().realmLevel().add(getRealmRole("delete"));
        }
    }

    public static void assignRole(UserEntity user) {
        UserRepresentation userRepresentation=getKeyCloakUser(user.getUsername());
        UsersResource usersResource= getUsersFromKeyCloak();
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.roles().realmLevel().add(getRealmRole(user.getRole()));
    }


    public int getIdOfUser(String username,String ip) throws ForbiddenAccessFromIpAddress {
        try {
            UserEntity user = getUser(username,ip);
            return user.getIdUser();
        }
        catch (NotFoundException exception){
            return  -1;
        }
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll().stream().filter(elem->elem.getIsDeleted()==0)
                .collect(Collectors.toList());
    }
    public void checkIfUsernameIsAlreadyInUse(String username) throws ConflictException {
                boolean used=userRepository.findAll().stream()
                        .anyMatch(elem->elem.getUsername().equals(username) && elem.getIsDeleted()==0);
                if(used){
                    throw  new ConflictException("Username is already in use.");
                }
    }

    public String getRole(String username,String ip) throws NotFoundException, ForbiddenAccessFromIpAddress {
        Optional<UserEntity>user=userRepository.findAll().stream().filter(elem->elem.getIsDeleted()==0 && elem.getUsername().equals(username))
                .findAny();
        if(user.isEmpty()){
            throw  new NotFoundException("User is not found");
        }
        UserEntity u=user.get();
        if(u.getRole().equals("client") && u.getIpAddress()!=null && !"".equals(u.getIpAddress()) && !u.getIpAddress().equals(ip)){
            throw new ForbiddenAccessFromIpAddress("Cannot access app from this ip adress.");
        }
        return u.getRole();
    }

    public void checkIp(String username, String ip) throws NotFoundException, ForbiddenAccessFromIpAddress {
        Optional<UserEntity>user=userRepository.findAll().stream().filter(elem->elem.getIsDeleted()==0 && elem.getUsername().equals(username))
                .findAny();
        if(user.isEmpty()){
            throw  new NotFoundException("User is not found");
        }
        UserEntity u=user.get();
        if(u.getRole().equals("client") && u.getIpAddress()!=null && !"".equals(u.getIpAddress()) && !u.getIpAddress().equals(ip)){
            throw new ForbiddenAccessFromIpAddress("Cannot access app from this ip adress.");
        }
    }
}