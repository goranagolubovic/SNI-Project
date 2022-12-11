package com.sni.dms.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private int idfile;
    private String name;
    private byte isDir;
    private Integer rootDir;
}
