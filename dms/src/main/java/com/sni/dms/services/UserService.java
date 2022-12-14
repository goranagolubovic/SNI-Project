package com.sni.dms.services;

import com.google.common.hash.Hashing;
import com.sni.dms.configuration.KeycloakProvider;
import com.sni.dms.configuration.TotpManager;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.repositories.FilesRepository;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.CodeRequest;
import com.sni.dms.requests.LoginRequest;
import com.sni.dms.responses.LoginResponse;
import lombok.SneakyThrows;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.io.File;
import java.nio.charset.StandardCharsets;
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
     private TotpManager totpManager;



    private Path root;

    public UserService(UserRepository userRepository, FilesRepository filesRepository, KeycloakProvider kcProvider, @Value("${PATH}") String path,TotpManager totpManager){
        this.userRepository = userRepository;
        this.filesRepository=filesRepository;
        this.kcProvider=kcProvider;
        this.root = Paths.get(path);
        this.totpManager=totpManager;
    }
    public LoginResponse login(CodeRequest codeRequest) {
       // getUsersFromKeyCloak().get(getKeyCloakUser(loginRequest.getUsername()).getCredentials().get(0).getValue());
       UserEntity user=findUser(codeRequest.getUsername());
        AccessTokenResponse accessTokenResponse = null;
        LoginResponse response = null;
        Keycloak keycloak = null;
        System.out.println("CODE"+codeRequest.getCode());
        if(totpManager.verifyCode(codeRequest.getCode(), user.getSecret())) {
            if (user.getIsDeleted() == 0) {
                System.out.println("yes");
                keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(codeRequest.getUsername(), user.getPassword()).build();

                try {
                    accessTokenResponse = keycloak.tokenManager().getAccessToken();
                    System.out.println(accessTokenResponse.getToken());
                    response = new LoginResponse(user, accessTokenResponse.getToken());
                } catch (BadRequestException ex) {
                    //LOG.warn("invalid account. User probably hasn't verified email.", ex);
                }
            }
        }
        return response;
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

    public boolean delete(String username) {
        Optional<UserEntity> user = userRepository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(username)).findAny();
        if(user.isPresent()){
            Optional<FileEntity> fileEntity=filesRepository.findAll().stream().filter(elem->elem.getName().equals(user.get().getUserDir())).findAny();
            fileEntity.ifPresent(f->{
                f.setIsDeleted((byte) 1);
                //prodji kroz sve fajlove i one koje je kreirao ovaj korisnik setuj na obrisano
                filesRepository.findAll().stream().forEach(elem-> {
                    if (elem.getRootDir()!=null && elem.getUserIdUser() == user.get().getIdUser()){
                        elem.setIsDeleted((byte) 1);
                        filesRepository.save(elem);

                }
                });
                filesRepository.save(f);
            });
            user.get().setIsDeleted((byte)1);
            userRepository.save(user.get());
            getUsersFromKeyCloak().get(getKeyCloakUser(username).getId()).remove();
    removeDefaultDirFromFileSystem(Path.of(user.get().getUserDir()));
            return  true;
        }
        return false;
    }

    @SneakyThrows
    private void removeDefaultDirFromFileSystem(Path pathToDir) {
        Files.walk(pathToDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public UserEntity updateUser(UserEntity user){

        UserEntity e = getUser(user.getUsername());
        if (e != null) {
            e.setUsername(user.getUsername());
            e.setPassword((user.getPassword()));
            e.setRole(user.getRole());
            e.setIpAddress(user.getIpAddress());
            e.setUserDir(user.getUserDir());
            e.setIsCreateApproved(user.getIsCreateApproved());
            e.setIsReadApproved(user.getIsReadApproved());
            e.setIsUpdateApproved(user.getIsUpdateApproved());
            e.setIsDeleteApproved(user.getIsDeleteApproved());
            return userRepository.save(e);
        }
        return null;

    }

    public UserEntity getUser(String username) {
        Optional<UserEntity> user = userRepository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(username)).findAny();
        if(user.isPresent() && user.get().getIsDeleted()==0){
            return user.get();
        }
        return null;
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

    public String getOldPassword(UserEntity user){

        Optional<UserEntity> optUser= userRepository.findAll().stream()
                .filter(e->e.getUsername().equals(user.getUsername())).findAny();
        return optUser.isPresent() ? optUser.get().getPassword() : "";
    }

    public int getIdOfUser(String username) {
        UserEntity user=getUser(username);
        return user.getIdUser();
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll().stream().filter(elem->elem.getIsDeleted()==0)
                .collect(Collectors.toList());
    }

    public boolean checkIfUsernameIsAlreadyInUse(String username) {
        return
                userRepository.findAll().stream()
                        .anyMatch(elem->elem.getUsername().equals(username) && elem.getIsDeleted()==0);
    }

    public UserEntity findUser(String username){
        Optional<UserEntity>user= userRepository.findAll().stream().filter(elem->elem.getUsername().equals(username)).findAny();
        return user.isPresent() ? user.get() : null;
    }

    public UserEntity checkCredentials(LoginRequest request) {
        String hashReqPassword = Hashing.sha512().hashString(request.getPassword(), StandardCharsets.UTF_8).toString();
        System.out.println(hashReqPassword);
        Optional<UserEntity> user = userRepository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(request.getUsername())
                        && elem.getPassword().equals(hashReqPassword)).findFirst();
        return user.isPresent() ? user.get():null;
    }
}