package com.sni.dms.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class DeleteRequest {
    String path;
    String username;
}
