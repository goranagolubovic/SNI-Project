package com.sni.dms.responses;

import com.sni.dms.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private UserEntity user;
    private String loginMessage;
    private int status;
}
