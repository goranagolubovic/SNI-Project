package com.sni.dms.controllers;

import com.sni.dms.configuration.TotpManager;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.exceptions.WrongAuthCodeException;
import com.sni.dms.records.ResponseRecord;
import com.sni.dms.responses.UserInfoResponse;
import com.sni.dms.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {
    private final UserService service;
    private TotpManager totpManager;
    public UserController(UserService service, TotpManager totpManager) {
        this.service = service;
        this.totpManager=totpManager;
    }

    @PostMapping("/auth")
    public ResponseEntity<ResponseRecord> login(@RequestBody String username){
        UserEntity user = null;
        try {
            user = service.findUser(username);
        } catch (NotFoundException e) {
            return ResponseEntity.ok(new ResponseRecord(404,e.getMessage()));
        }
        String img="";
        if(user.getIsFirstSignIn()==1){
             img=totpManager.getUriForImage(user.getSecret());
        }
        //bespotreban kod, imaju sad svi secret
//        if(user.getSecret()==null){
//            user.setSecret(totpManager.generateSecret());
//            try {
//                service.updateUser(user);
//            }
//            catch (NotFoundException exception){
//                return ResponseEntity.ok(exception.getMessage());
//            }
        return ResponseEntity.ok(new ResponseRecord(200,img));
    }
    @PostMapping("/info")
    public ResponseEntity<UserInfoResponse> getInfo(@RequestBody String username){
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        try {
            userInfoResponse.setUser(service.getUser(username));
            userInfoResponse.setStatus(200);
            return ResponseEntity.ok(userInfoResponse);
        }
        catch (NotFoundException e) {
            userInfoResponse.setLoginMessage(e.getMessage());
            userInfoResponse.setStatus(404);
            return ResponseEntity.ok(userInfoResponse);
        }
    }
}
