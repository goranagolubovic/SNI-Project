package com.sni.dms.controllers;

import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.CreateUserRequest;
import com.sni.dms.service.KeycloakAdminClientService;
import com.sni.dms.services.FilesService;
import com.sni.dms.services.UserService;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@CrossOrigin("*")
public class AdminController {
    @Autowired
    UserRepository repository;
    private final KeycloakAdminClientService kcAdminClient;
    private final UserService service;
    private final FilesService filesService;


    public AdminController(KeycloakAdminClientService kcAdminClient,UserService service,FilesService filesService) {
        this.kcAdminClient = kcAdminClient;
        this.service=service;
        this.filesService=filesService;
    }

    @PostMapping("/admin/users")
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity user){
        System.out.println("try");
        CreateUserRequest userRequest=new CreateUserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(user.getPassword());
        userRequest.setFirstname("");
        userRequest.setEmail("");
        userRequest.setLastname("");
        service.createDefaultDirForUser(user.getUserDir());

        FileEntity fileEntity=new FileEntity();
        fileEntity.setName(user.getUserDir());
        //hardkodovano 1,treba popraviti
        fileEntity.setRootDir(1);
        fileEntity.setIsDir((byte) 1);

        filesService.addNewFile(fileEntity);
        Response createdResponse = kcAdminClient.createKeycloakUser(userRequest);

        UserRepresentation userRepresentation=service.getKeyCloakUser(user.getUsername());
        UsersResource usersResource= service.getUsersFromKeyCloak();
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.roles().realmLevel().add(service.getRealmRole(user.getRole()));
        int result=createdResponse.getStatus();
        if(result==409){
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok(repository.save(user));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserEntity>> getUsers(){
        System.out.println("trying to get users");
        return ResponseEntity.ok(repository.findAll());
    }

    @PutMapping("/admin/users")
    public ResponseEntity<UserEntity>updateUser(@RequestBody UserEntity user){
    UserEntity userResponse=service.updateUser(user);
    if(userResponse==null){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    else{
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    }
    @DeleteMapping(value = "/admin/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {

        var isRemoved = service.delete(username);

        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @GetMapping(value = "/admin/users/{username}")
    public ResponseEntity<UserEntity> getUser(@PathVariable String username) {

        var user = service.getUser(username);

        if (user==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
