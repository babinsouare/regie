package com.api.regie.utils;

import com.api.regie.models.Users;
import com.api.regie.services.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    final UsersService  usersService;

    public SecurityUtils(UsersService usersService) {
        this.usersService = usersService;
    }

    public Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }

        return usersService.findByEmail(auth.getName()).orElse(null);
    }
}
