package com.sni.dms.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogsResponse {
    String username;
    String filePath;
    String action;
    Timestamp dateTime;
}
