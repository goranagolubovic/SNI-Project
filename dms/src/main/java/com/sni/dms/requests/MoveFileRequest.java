package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveFileRequest {
    String destinationDir;
    String filePath;
    String fileName;
    String username;
}