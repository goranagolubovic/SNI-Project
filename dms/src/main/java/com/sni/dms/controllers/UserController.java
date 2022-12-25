package com.sni.dms.controllers;

import com.sni.dms.exceptions.ForbiddenAccessFromIpAddress;
import com.sni.dms.exceptions.NotFoundException;
import com.sni.dms.records.ResponseRecord;
import com.sni.dms.requests.ChangePasswordRequest;
import com.sni.dms.responses.UserInfoResponse;
import com.sni.dms.service.KeycloakAdminClientService;
import com.sni.dms.services.UserService;
import com.sni.dms.utils.HttpUtils;
import org.keycloak.authorization.client.util.Http;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin("*")
public class UserController {
    private final UserService service;
    private  final KeycloakAdminClientService keycloakAdminClientService;
    public UserController(UserService service, KeycloakAdminClientService keycloakAdminClientService) {
        this.service = service;
        this.keycloakAdminClientService=keycloakAdminClientService;
    }
    @PostMapping("/info")
    public ResponseEntity<UserInfoResponse> getInfo(@RequestBody String username, HttpServletRequest request){
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        String ip = HttpUtils.getRequestIP(request);
        System.out.println("IP Adress is "+ip);
        try {
            userInfoResponse.setUser(service.getUser(username,ip));
            userInfoResponse.setStatus(200);
            return ResponseEntity.ok(userInfoResponse);
        }
        catch (NotFoundException e1) {
            userInfoResponse.setLoginMessage(e1.getMessage());
            userInfoResponse.setStatus(404);
            return ResponseEntity.ok(userInfoResponse);
        } catch (ForbiddenAccessFromIpAddress e2) {
            userInfoResponse.setLoginMessage(e2.getMessage());
            userInfoResponse.setStatus(500);
            return ResponseEntity.ok(userInfoResponse);
        }
    }
    @PostMapping("/role")
    public ResponseEntity<ResponseRecord> getRole(@RequestBody String username, HttpServletRequest httpServletRequest){
        String ip = HttpUtils.getRequestIP(httpServletRequest);
        try {
            String role=service.getRole(username,ip);
            return ResponseEntity.ok(new ResponseRecord(200,role));
        } catch (NotFoundException e1) {
            return ResponseEntity.ok(new ResponseRecord(404,e1.getMessage()));
        } catch (ForbiddenAccessFromIpAddress e2) {
            return ResponseEntity.ok(new ResponseRecord(500,e2.getMessage()));
        }
    }

    @PutMapping ("/changePassword")
    public ResponseEntity<ResponseRecord> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest httpServletRequest){
        String ip = HttpUtils.getRequestIP(httpServletRequest);
        try {
            service.checkIp(changePasswordRequest.getUsername(),ip);
            keycloakAdminClientService.changePassword(changePasswordRequest);
            return ResponseEntity.ok(new ResponseRecord(200,"Password is changed successfully"));
        } catch (NotFoundException e1) {
            return ResponseEntity.ok(new ResponseRecord(404,e1.getMessage()));
        } catch (ForbiddenAccessFromIpAddress e2) {
            return ResponseEntity.ok(new ResponseRecord(500,e2.getMessage()));
        }
    }

}
