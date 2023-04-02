package com.example.AuthenticateServer.service;

import com.example.AuthenticateServer.config.ClientConfig;
import com.example.AuthenticateServer.model.ClientDetail;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 君墨笑
 * @date 2023/3/23
 */
@Service
public class MyClientDetailsService implements ClientDetailsService, InitializingBean {

    @Autowired
    private ClientConfig config;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Map<String,ClientDetails> map = new HashMap<>();

    /**
     * 自定义客户端信息获取Service
     * 从配置文件读取放入Map中
     * 自己构建BaseClientDetails，客户端秘钥需要Encoder加密
     *
     * @param clientId
     * @return
     * @throws ClientRegistrationException
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return map.get(clientId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ClientDetail> list = config.getConfig();
        list.forEach(it -> {
            BaseClientDetails clientDetails = new BaseClientDetails(it.getClientId(),it.getResourceId(),it.getScope(),it.getGrantType(),null,it.getRedirectUri());
            clientDetails.setClientSecret(passwordEncoder.encode(it.getSecret()));
            map.put(it.getClientId(),clientDetails);
        });
    }
}
