package com.sni.dms.controllers;

import com.sni.dms.configuration.TotpManager;
import com.sni.dms.entities.UserEntity;
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
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        UserEntity user=service.checkCredentials(request);
        if(user!=null) {
            return ResponseEntity.ok(totpManager.getUriForImage(user.getSecret()));
        }
        else{
            //hardkodovana poruka
            return ResponseEntity.ok("Bad credentials");
        }
    }
    @PostMapping("/code")
    public ResponseEntity<LoginResponse> checkCode(@RequestBody CodeRequest request){
        System.out.println("Code is ");
        System.out.println(request.getCode());
        LoginResponse loginResponse = service.login(request);
        return ResponseEntity.ok(loginResponse);
    }
}
