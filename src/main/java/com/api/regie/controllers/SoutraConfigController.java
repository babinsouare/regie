package com.api.regie.controllers;

import com.api.regie.models.Result;
import com.api.regie.models.SoutraConfig;
import com.api.regie.services.SoutraConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/soutra-config")
public class SoutraConfigController {

    private final SoutraConfigService soutraConfigService;

    public SoutraConfigController(SoutraConfigService soutraConfigService) {
        this.soutraConfigService = soutraConfigService;
    }

    @GetMapping("liste")
    public Result<?> getAllSoutraConfigs() {
        return Result.success(soutraConfigService.getAllSoutraConfigs(),
                "Liste des configurations Soutra.",
                "List of Soutra configurations.",
                "Lista das configurações Soutra.");
    }

    @GetMapping("getbyid")
    public Result<?> getSoutraConfigById(@RequestParam("id") UUID id) {
        Optional<SoutraConfig> configOpt = soutraConfigService.findById(id);
        if (configOpt.isEmpty()) {
            return Result.error(404,
                    "Configuration Soutra introuvable.",
                    "Soutra configuration not found.",
                    "Configuração Soutra não encontrada.");
        }
        return Result.success(configOpt.get(),
                "Configuration Soutra trouvée.",
                "Soutra configuration found.",
                "Configuração Soutra encontrada.");
    }

    @PostMapping("/add")
    public Result<?> addSoutraConfig(@RequestBody SoutraConfig soutraConfig) {
        return Result.success(soutraConfigService.addSoutraConfig(soutraConfig),
                "Configuration Soutra ajoutée (inactive).",
                "Soutra configuration added (inactive).",
                "Configuração Soutra adicionada (inativa).");
    }

    @PutMapping("/update")
    public Result<?> updateSoutraConfig(@RequestParam("id") UUID id, @RequestBody SoutraConfig soutraConfig) {
        Optional<SoutraConfig> existingOpt = soutraConfigService.findById(id);
        if (existingOpt.isEmpty()) {
            return Result.error(404,
                    "Configuration Soutra introuvable.",
                    "Soutra configuration not found.",
                    "Configuração Soutra não encontrada.");
        }
        SoutraConfig existing = existingOpt.get();
        existing.setEnvironnement(soutraConfig.getEnvironnement());
        existing.setBaseUrl(soutraConfig.getBaseUrl());
        existing.setClientId(soutraConfig.getClientId());
        existing.setClientSecret(soutraConfig.getClientSecret());
        existing.setApiKey(soutraConfig.getApiKey());

        return Result.success(soutraConfigService.updateSoutraConfig(existing),
                "Configuration Soutra modifiée.",
                "Soutra configuration updated.",
                "Configuração Soutra alterada.");
    }

    @PutMapping("/activer")
    public Result<?> activerSoutraConfig(@RequestParam("id") UUID id) {
        try {
            return Result.success(soutraConfigService.activer(id),
                    "Configuration Soutra activée.",
                    "Soutra configuration activated.",
                    "Configuração Soutra ativada.");
        } catch (RuntimeException ex) {
            return Result.error(404, ex.getMessage(), ex.getMessage(), ex.getMessage());
        }
    }
}
