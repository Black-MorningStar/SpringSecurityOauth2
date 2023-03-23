package com.example.AuthenticateServer.config;

import com.example.AuthenticateServer.model.ClientDetail;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 君墨笑
 * @date 2023/3/23
 */
@Configuration
@ConfigurationProperties(prefix = "clients")
@Getter
@Setter
public class ClientConfig {

    private List<ClientDetail> config;
}
