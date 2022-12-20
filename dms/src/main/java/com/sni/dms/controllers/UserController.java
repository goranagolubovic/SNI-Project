package com.sni.dms.controllers;

import com.sni.dms.configuration.TotpManager;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.exceptions.WrongAuthCodeException;
import com.sni.dms.records.ResponseRecord;
import com.sni.dms.requests.CodeRequest;
import com.sni.dms.requests.LoginRequest;
import com.sni.dms.responses.LoginResponse;
import com.sni.dms.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.CoderResult;

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
    public ResponseEntity<ResponseRecord> login(@RequestBody LoginRequest request){
        try {
            UserEntity user = service.checkCredentials(request);
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
        catch (NotFoundException exception){
            return ResponseEntity.ok(new ResponseRecord(404, exception.getMessage()));
        }
    }
    @PostMapping("/code")
    public ResponseEntity<LoginResponse> checkCode(@RequestBody CodeRequest request){
        LoginResponse loginResponse = new LoginResponse();
        try {
            loginResponse = service.login(request);
            loginResponse.getUser().setPassword(null);
            return ResponseEntity.ok(loginResponse);
        }
        catch (WrongAuthCodeException exception){
            loginResponse.setLoginMessage(exception.getMessage());
            return ResponseEntity.ok(loginResponse);
        }
    }
}
