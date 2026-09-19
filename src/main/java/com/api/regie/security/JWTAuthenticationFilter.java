package com.api.regie.security;

import com.api.regie.models.PermissionUsers;
import com.api.regie.models.Result;
import com.api.regie.models.Users;
import com.api.regie.services.PermissionUsersService;
import com.api.regie.services.UsersService;
import com.api.regie.utils.SecParans;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    final AuthenticationManager authenticationManager;

    ObjectMapper objectMapper = new ObjectMapper();

    final UsersService usersService;
    final PermissionUsersService permissionUsersService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService, PermissionUsersService permissionUsersService) {
        this.authenticationManager = authenticationManager;
        this.usersService = usersService;
        this.permissionUsersService = permissionUsersService;

        setFilterProcessesUrl("/auth/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {

        Users authRequest;
        try {
            authRequest = new ObjectMapper().readValue(req.getInputStream(), Users.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(400,
                "Login incorrect.",
                "Invalid credentials.",
                "Credenciais inválidas.")));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        User user = (User) auth.getPrincipal();
        Optional<Users> mineUser = usersService.findByEmail(user.getUsername());
        if (mineUser.isEmpty()) {
            res.getWriter()
                    .write(objectMapper.writeValueAsString(Result.error(400,
                            "Login incorrect.",
                            "Invalid credentials.",
                            "Credenciais inválidas.")));
        }
        else if(!mineUser.get().getBtEnabled()) {
            res.getWriter()
                    .write(objectMapper.writeValueAsString(Result.error(400,
                            "Impossible de se connecter avec ce compte.",
                            "Unable to sign in with this account.",
                            "Não é possível iniciar sessão com esta conta.")));
        } else {
            String jwt = JWT
                    .create()
                    .withSubject(user.getUsername())
                    //.withClaim("roles", mineUser.get().getRoles().stream().map(r -> r.getCode()).toList())
                    .withClaim("userId", mineUser.get().getId().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecParans.EXP_TIME))
                    .sign(Algorithm.HMAC256(SecParans.SECRET));

            List<PermissionUsers> permissions = permissionUsersService.getPermissionUsersByUser(mineUser.get());

            Map<String,Object> authResponse = new HashMap<>();

            authResponse.put("token",jwt);
            authResponse.put("email",mineUser.get().getEmail());
            authResponse.put("data",mineUser.get());
            //authResponse.put("permissions",permissions.stream().map(r -> r.getPermissions().getCode()).toList());
            authResponse.put("permissions",permissions.stream().filter(r -> Boolean.TRUE.equals(r.getBtEnabled())).map(r -> r.getPermissions().getCode()).toList());


            res.setContentType("application/json");
            res.getWriter().write(objectMapper.writeValueAsString(Result.success(authResponse,
                    "Connexion réussie.",
                    "Sign-in successful.",
                    "Sessão iniciada com sucesso.")));

        }
    }
}




