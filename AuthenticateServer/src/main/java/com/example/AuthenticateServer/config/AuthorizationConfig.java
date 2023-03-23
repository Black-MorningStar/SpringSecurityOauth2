package com.example.AuthenticateServer.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 配置授权服务器
 *
 * @author 君墨笑
 * @date 2023/3/22
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    //自动注入下面配置的客户端注册信息
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthenticationManager providerManager;

    /**
     * 配置客户端注册信息 ClientDetailsService
     * 哪些客户端可以来请求授权服务获得授权码+token
     *
     * @param clients
     * @throws Exception
     */
    /*@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() //采用内存的方式存储客户端注册信息
                .withClient("c1") //客户端ID
                .secret(new BCryptPasswordEncoder().encode("123")) //客户端秘钥
                .resourceIds("resource01") //客户端可以访问的资源服务器ID
                //该客户端支持的授权方式：授权码、简化版、密码模式、客户端秘钥方式、刷新令牌
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token")
                .scopes("all") //该客户端获取数据的权限范围,是自定义的字符串，后面会用到
                .autoApprove(false) //是否自动通过授权（用户授权页面点击确认、拒绝）
                .redirectUris("http://www.baidu.com"); //授权成功后回调的URL,后面会附带上授权码
    }*/

    /**
     * 配置客户端注册信息 ClientDetailsService
     * 哪些客户端可以来请求授权服务获得授权码+token
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient()
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 配置令牌的访问端点end point和令牌服务token service
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(providerManager); //密码模式下需要配置认证管理器,认证之后直接获取access_token
        endpoints.authorizationCodeServices(codeServices()); //授权码模式下需要配置,用来确认授权码生成及存储的方式
        endpoints.tokenServices(tokenServices());//配置令牌服务
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.POST); //令牌端点的访问方式
        //endpoints.pathMapping("/oauth/authorize","/oauth/myself/authorize"); //自定义授权端点路径
        //endpoints.pathMapping("/oauth/token","/oauth/myself/token"); //自定义令牌端点路径
        //endpoints.pathMapping("/oauth/confirm_access","/oauth/myself/confirm_access"); //自定义用户确认授权提交端点路径
        //endpoints.pathMapping("/oauth/error","/oauth/myself/error"); //自定义授权服务错误信息路径
        //endpoints.pathMapping("/oauth/check_token","/oauth/myself/check_token"); //自定义校验token路径
    }


    /**
     * 配置令牌访问端点的安全约束
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()") //获取token的端点全部放行
                .checkTokenAccess("permitAll()")  //校验token的端点全部放行
                .allowFormAuthenticationForClients(); // 允许表单认证
    }

    /**
     * 令牌存储策略
     * 使用JWT的方式,就不会存储令牌而是动态生成
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(tokenConverter());
    }


    /**
     * 创建Jwt令牌生成策略
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey("shushushu"); //加签名秘钥防止篡改
        return tokenConverter;
    }

    /**
     * 配置令牌服务Service
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore()); //配置令牌生产+存储策略,使用JWT的方式
        //使用Jwt作为令牌生成策略，则需要增强令牌
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Lists.newArrayList(tokenConverter()));
        defaultTokenServices.setTokenEnhancer(enhancerChain);
        defaultTokenServices.setSupportRefreshToken(true); //支持刷新令牌
        defaultTokenServices.setAccessTokenValiditySeconds(7200); //令牌默认失效时间2小时
        defaultTokenServices.setRefreshTokenValiditySeconds(7200); //刷新令牌失效时间2小时
        defaultTokenServices.setClientDetailsService(clientDetailsService); //注入客户端配置服务
        return defaultTokenServices;
    }

    /**
     * 配置授权码服务
     * 授权码是一次性有效的,一个授权码可以申请一次access_token
     * 授权码+用户认证信息会存储在内存的Map中
     * 在获取access_token时会根据授权码获取用户认证信息,获取到之后生成对应的用户access_token
     * 获取一次之后就会移除授权码+用户认证信息的映射,这样可以保证一个授权码只能使用一次
     */
    @Bean
    public AuthorizationCodeServices codeServices() {
        InMemoryAuthorizationCodeServices inMemoryAuthorizationCodeServices = new InMemoryAuthorizationCodeServices();
        return inMemoryAuthorizationCodeServices;
    }
}
