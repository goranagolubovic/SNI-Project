package com.sni.dms.services;

import com.google.common.hash.Hashing;
import com.sni.dms.configuration.KeycloakProvider;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.LoginRequest;
import com.sni.dms.responses.LoginResponse;
import com.sni.dms.service.KeycloakAdminClientService;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

   private UserRepository repository;
    private final KeycloakProvider kcProvider;



    private Path root;

    public UserService(UserRepository repository, KeycloakProvider kcProvider, @Value("${PATH}") String path){
        this.repository=repository;
        this.kcProvider=kcProvider;
        this.root = Paths.get(path);
    }
    public LoginResponse login(LoginRequest loginRequest) {
        String hashReqPassword = Hashing.sha512().hashString(loginRequest.getPassword(), StandardCharsets.UTF_8).toString();
        System.out.println(hashReqPassword);
        Optional<UserEntity> user = repository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(loginRequest.getUsername())
                        && elem.getPassword().equals(hashReqPassword)).findFirst();
        AccessTokenResponse accessTokenResponse = null;
        LoginResponse response = null;
        Keycloak keycloak = null;
        if (user.isPresent()) {
            System.out.println("yes");
            keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword()).build();

            try {
                accessTokenResponse = keycloak.tokenManager().getAccessToken();
                System.out.println(accessTokenResponse.getToken());
                response = new LoginResponse(user.get(), accessTokenResponse.getToken());
            } catch (BadRequestException ex) {
                //LOG.warn("invalid account. User probably hasn't verified email.", ex);
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
        Optional<UserEntity> user = repository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(username)).findAny();
                if(user.isPresent()){
                    repository.delete(user.get());
                    getUsersFromKeyCloak().get(getKeyCloakUser(username).getId()).remove();
                    return  true;
                }
        return false;
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
                return repository.save(e);
            }
            return null;

    }

    public UserEntity getUser(String username) {
        Optional<UserEntity> user = repository.findAll().stream()
                .filter(elem -> elem.getUsername().equals(username)).findAny();
        if(user.isPresent()){
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
}
