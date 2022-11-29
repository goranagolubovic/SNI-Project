package com.sni.dms.requests;

import com.sni.dms.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadFileRequest {
    private String userDir;
    private String filePath;
}
