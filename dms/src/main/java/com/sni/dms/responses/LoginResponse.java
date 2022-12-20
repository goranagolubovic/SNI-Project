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
public class LoginResponse {
    private UserEntity user;
    private String token;
    private String loginMessage;
}
