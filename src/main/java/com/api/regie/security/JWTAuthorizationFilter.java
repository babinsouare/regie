package com.api.regie.security;


import com.api.regie.models.Result;
import com.api.regie.utils.SecParans;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");
        final ObjectMapper objectMapper = new ObjectMapper();

        if (jwt == null || !jwt.startsWith(SecParans.PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParans.SECRET)).build();
        String username = null;
        DecodedJWT decodedJWT;

        jwt = jwt.replace(SecParans.PREFIX, "");

        if (jwt.isBlank()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(100,
                    "Token invalide.",
                    "Invalid token.",
                    "Token inválido.")));
        }

        if (!jwt.isBlank()) {
            try {
                decodedJWT = verifier.verify(jwt);

            }catch (MalformedJwtException ex) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(100,
                        "Token invalide.",
                        "Invalid token.",
                        "Token inválido.")));
                return;
            }
            catch (ExpiredJwtException ex) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(100,
                        "Session expirée, merci de vous reconnecter.",
                        "Your session has expired, please sign in again.",
                        "A sua sessão expirou, inicie sessão novamente.")));

                return;
            }
            catch (UnsupportedJwtException ex) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(100,
                        "Token invalide.",
                        "Invalid token.",
                        "Token inválido.")));
                return;
            }
            catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(100,
                        "Token invalide.",
                        "Invalid token.",
                        "Token inválido.")));
                return;

            }
            username = decodedJWT.getSubject();

        }

        Collection<GrantedAuthority> authorities = List.of();

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(user);
        filterChain.doFilter(request, response);

    }
}

