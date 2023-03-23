package com.example.ResourceServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 配置资源服务器
 *
 * @author 君墨笑
 * @date 2023/3/23
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    /**
     * 配置资源服务器
     *
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource01")
                .tokenStore(tokenStore());
    }


    /**
     * 配置Security
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(authorize -> authorize.antMatchers("/**").access("#oauth2.hasScope('all')")
                        .anyRequest().authenticated()
                );
    }



    /**
     * 实例化令牌存储策略
     * 使用JWT的方式,就不会存储令牌
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
}
