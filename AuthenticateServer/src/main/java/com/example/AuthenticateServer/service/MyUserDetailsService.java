package com.example.AuthenticateServer.service;

import com.google.common.collect.Lists;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author 君墨笑
 * @date 2023/3/9
 */

public class MyUserDetailsService implements UserDetailsService  {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.contentEquals("psx")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return new MyUserDetails(username,encoder.encode("123"), Lists.newArrayList("ADMIN"));
        }
        return null;
    }
}
