package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditFileRequest {
    private String fileContent;
    private String filePath;
    private String username;
}