package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadFileRequest {
    private String action;
    private String userDir;
    private String filePath;
    private String username;
}
