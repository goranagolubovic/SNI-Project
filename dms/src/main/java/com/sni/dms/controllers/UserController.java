package com.sni.dms.controllers;

import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.responses.UserInfoResponse;
import com.sni.dms.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
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
