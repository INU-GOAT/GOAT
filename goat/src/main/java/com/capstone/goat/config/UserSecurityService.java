package com.capstone.goat.config;

import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login_id) throws UsernameNotFoundException {
        com.capstone.goat.domain.User _user = userRepository.findByLogin_id(login_id).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("hen715".equals(login_id)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getKey()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.USER.getKey()));
        }
        return new User(_user.getLogin_id(), _user.getPassword(), authorities);

    }
}
