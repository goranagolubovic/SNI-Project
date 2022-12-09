package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableDirsRequest {
    String userDir;
    String currentDir;
}