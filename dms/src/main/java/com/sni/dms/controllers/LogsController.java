package com.sni.dms.controllers;

import com.sni.dms.entities.LogsEntity;
import com.sni.dms.requests.LoginRequest;
import com.sni.dms.responses.LoginResponse;
import com.sni.dms.responses.LogsResponse;
import com.sni.dms.services.LogsService;
import com.sni.dms.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class LogsController {
    private final LogsService logsService;

    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @GetMapping("/logs")
    public ResponseEntity<List<LogsResponse>> listLogs(){
        return ResponseEntity.ok(logsService.readAllLogs());
    }
}
