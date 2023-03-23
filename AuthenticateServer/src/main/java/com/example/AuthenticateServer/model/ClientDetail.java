package com.example.AuthenticateServer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 君墨笑
 * @date 2023/3/23
 */
@Getter
@Setter
public class ClientDetail {

    private String clientId;

    private String secret;

    private String resourceId;

    private String grantType;

    private String scope;

    private String redirectUri;
}
