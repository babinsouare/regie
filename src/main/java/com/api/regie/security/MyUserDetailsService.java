package com.api.regie.security;


import com.api.regie.models.Users;
import com.api.regie.services.UsersService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    final UsersService usersService;

    public MyUserDetailsService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Optional<Users> users = usersService.findByEmail("babinsouare@gmail.com");
        Optional<Users> users = usersService.findByEmail(username);
        Collection<? extends GrantedAuthority> authorities = List.of();
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur introuvable");
        }

        Users user = users.get();

        return new User(user.getEmail(), user.getPassword(), authorities);

    }
}

