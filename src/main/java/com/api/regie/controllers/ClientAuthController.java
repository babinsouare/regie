package com.api.regie.controllers;

import com.api.regie.models.Clients;
import com.api.regie.models.Result;
import com.api.regie.services.ClientsService;
import com.api.regie.utils.SecParans;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/client/auth")
public class ClientAuthController {

    private final ClientsService clientsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ClientAuthController(ClientsService clientsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clientsService = clientsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginRequest) {

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Result.error(400,
                    "L'e-mail et le mot de passe sont obligatoires.",
                    "Email and password are required.",
                    "O e-mail e a palavra-passe são obrigatórios.");
        }

        Optional<Clients> clientOpt = clientsService.findByEmailResponsable(email);

        if (clientOpt.isEmpty()) {
            return Result.error(400, "Login incorrect.", "Invalid credentials.", "Credenciais inválidas.");
        }

        Clients client = clientOpt.get();

        if (!Boolean.TRUE.equals(client.getBtEnabled())) {
            return Result.error(400,
                    "Ce compte client est désactivé.",
                    "This client account is deactivated.",
                    "Esta conta de cliente está desativada.");
        }

        if (client.getPassword() == null || !bCryptPasswordEncoder.matches(password, client.getPassword())) {
            return Result.error(400, "Login incorrect.", "Invalid credentials.", "Credenciais inválidas.");
        }

        String jwt = JWT.create()
                .withSubject(client.getEmailResponsable())
                .withClaim("clientId", client.getId().toString())
                .withClaim("role", "CLIENT")
                .withExpiresAt(new Date(System.currentTimeMillis() + SecParans.EXP_TIME))
                .sign(Algorithm.HMAC256(SecParans.SECRET));

        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("token", jwt);
        authResponse.put("email", client.getEmailResponsable());
        authResponse.put("data", client);

        return Result.success(authResponse, "Connexion réussie.", "Sign-in successful.", "Sessão iniciada com sucesso.");
    }
}
