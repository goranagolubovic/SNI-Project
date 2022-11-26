package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFileRequest {
    String rootDir;
    String name;
    int isDir;
}
