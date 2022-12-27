package com.sni.dms.controllers;

import com.google.gson.Gson;
import com.sni.dms.entities.FileEntity;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.exceptions.ConflictException;
import com.sni.dms.exceptions.ForbiddenAccessFromIpAddress;
import com.sni.dms.exceptions.InternalServerError;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.records.ResponseRecord;
import com.sni.dms.repositories.UserRepository;
import com.sni.dms.requests.CreateUserRequest;
import com.sni.dms.requests.UserRequest;
import com.sni.dms.service.KeycloakAdminClientService;
import com.sni.dms.services.FilesService;
import com.sni.dms.services.UserService;
import com.sni.dms.utils.HttpUtils;
import org.keycloak.authorization.client.util.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
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
    public ResponseEntity<ResponseRecord> saveUser(@RequestBody UserRequest uR, HttpServletRequest httpServletRequest){
        System.out.println("try");
        String ip = HttpUtils.getRequestIP(httpServletRequest);
        UserEntity user= uR.getUser();;
        try {
            service.checkIfUsernameIsAlreadyInUse(user.getUsername());

            CreateUserRequest userRequest = new CreateUserRequest();
            userRequest.setUsername(user.getUsername());
            userRequest.setPassword(uR.getPassword());
            userRequest.setFirstname("");
            userRequest.setEmail("");
            userRequest.setLastname("");
            service.createDefaultDirForUser(user.getUserDir());
            user.setIsDeleted((byte) 0);
            user.setIsPasswordChanged((byte)0);
            UserEntity createdUser = repository.save(user);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(user.getUserDir());
            //hardkodovano 1,treba popraviti
            fileEntity.setRootDir(1);
            fileEntity.setIsDir((byte) 1);
            fileEntity.setIsDeleted((byte) 0);
            int userId = service.getUser(userRequest.getUsername(),ip).getIdUser();
            fileEntity.setUserIdUser(userId);

            filesService.addNewFile(fileEntity);
            Response createdResponse = kcAdminClient.createKeycloakUser(userRequest);


            service.assignRole(user);
            service.assignCRUDPrivilegis(user);

            return ResponseEntity.ok(new ResponseRecord(200, ""));
//vjerovatno za ovim nema potrebe
//            int result = createdResponse.getStatus();
//            if (result == 409) {
//                return ResponseEntity.status(409).build();
//            }
        }
        catch (ConflictException ex1){
            return ResponseEntity.ok(new ResponseRecord(409, ex1.getMessage()));
        }
        catch (NotFoundException ex2){
            return ResponseEntity.ok(new ResponseRecord(404, ex2.getMessage()));
        } catch (ForbiddenAccessFromIpAddress e) {
            return ResponseEntity.ok(new ResponseRecord(500, e.getMessage()));
        }

    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserEntity>> getUsers(){
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PutMapping("/admin/users")
    public ResponseEntity<ResponseRecord>updateUser(@RequestBody UserRequest user,HttpServletRequest httpServletRequest){
        String ip = HttpUtils.getRequestIP(httpServletRequest);
        try {
//            //service.checkIfUsernameIsAlreadyInUse(user.getUsername());
//            //ako je prazno polje sifra, ostavi staru sifru iz keycloaka
//            if ("".equals(user.getPassword())) {
//                user.setPassword(service.getOldPassword(user));
//            }
//            //ako nije sacuvaj hash lozinke u bazu
//            else {
//                user.setPassword(Hashing.sha512().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());
//            }
            user.getUser().setIsDeleted((byte) 0);
            user.getUser().setIsPasswordChanged((byte) 0);
            service.updateUser(user.getUser(),ip);
            kcAdminClient.updateKeyCloakUser(user);


                service.assignRole(user.getUser());
                service.assignCRUDPrivilegis(user.getUser());

                return  ResponseEntity.ok(new ResponseRecord(200,""));


        }
        catch(NotFoundException e2){
            return  ResponseEntity.ok(new ResponseRecord(404,e2.getMessage()));
        } catch (ForbiddenAccessFromIpAddress e) {
            return  ResponseEntity.ok(new ResponseRecord(500,e.getMessage()));
        }
    }
    @DeleteMapping(value = "/admin/users/{username}")
    public ResponseEntity<ResponseRecord> deleteUser(@PathVariable String username, HttpServletRequest httpServletRequest) {
        //String ip = HttpUtils.getRequestIP(httpServletRequest);
    try {
        service.delete(username);
        return ResponseEntity.ok(new ResponseRecord(200,""));
    }
    catch (NotFoundException exception){
        return ResponseEntity.ok(new ResponseRecord(404,exception.getMessage()));
    } catch (InternalServerError e) {
        return ResponseEntity.ok(new ResponseRecord(500,e.getMessage()));
    }
    }

    @GetMapping(value = "/admin/users/{username}")
    public ResponseEntity<String> getUser(@PathVariable String username, HttpServletRequest httpServletRequest) {
        String ip = HttpUtils.getRequestIP(httpServletRequest);
        try {
            var user = service.getUser(username,ip);
            String userJson=new Gson().toJson(user);
            return ResponseEntity.ok(userJson);
        }
        catch (NotFoundException exception){
            ResponseRecord responseRecord=new ResponseRecord(404,exception.getMessage());
            String jsonRecord=new Gson().toJson(responseRecord);
            return ResponseEntity.ok(jsonRecord);
        } catch (ForbiddenAccessFromIpAddress e) {
            ResponseRecord responseRecord=new ResponseRecord(500,e.getMessage());
            String jsonRecord=new Gson().toJson(responseRecord);
            return ResponseEntity.ok(jsonRecord);
        }

    }
}