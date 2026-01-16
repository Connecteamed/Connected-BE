package com.connecteamed.server.global.auth.service;

import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;

public interface AuthQueryService {

        AuthResDTO.LoginDTO login(AuthReqDTO.LoginDTO dto);

        AuthResDTO.RefreshResultDTO reissue(AuthReqDTO.ReissueDTO dto);
}
