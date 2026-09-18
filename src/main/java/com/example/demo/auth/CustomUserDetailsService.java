package com.example.demo.auth;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.users.User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("使用者不存在: " + username));

        List<GrantedAuthority> authorities = RoleAuthorityMapper.fromRoleName(
                user.getRole() != null ? user.getRole().getRoleName() : "");

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getStatus() == com.example.demo.users.UserStatus.LOCKED)
                .disabled(user.getStatus() == com.example.demo.users.UserStatus.INACTIVE)
                .build();
    }
}
