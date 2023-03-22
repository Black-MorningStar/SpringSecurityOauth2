package com.example.AuthenticateServer.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 君墨笑
 * @date 2023/3/9
 */
public class MyUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<String> permissions;

    public MyUserDetails(String username, String password, List<String> permissions) {
        this.password = password;
        this.username = username;
        this.permissions = permissions;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        List<SimpleGrantedAuthority> list = permissions.stream().map(it -> new SimpleGrantedAuthority(it)).collect(Collectors.toList());
        return list;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
