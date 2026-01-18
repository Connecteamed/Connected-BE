package com.connecteamed.server.global.auth.service;

import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;

public interface AuthCommandService {
    AuthResDTO.JoinDTO signup(AuthReqDTO.JoinDTO dto);
}
