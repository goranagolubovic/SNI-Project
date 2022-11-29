package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileRequest {
    private String folderName;
    private MultipartFile file;
}
