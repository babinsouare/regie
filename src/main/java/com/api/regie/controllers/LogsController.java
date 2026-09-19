package com.api.regie.controllers;

import com.api.regie.models.Logs;
import com.api.regie.models.Result;
import com.api.regie.services.LogsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/logs")
public class LogsController {

    private final LogsService logsService;

    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @GetMapping("liste")
    public Result getAllLogs(){
        return Result.success(logsService.getAllLogs(), "Liste des logs.", "List of logs.", "Lista dos registos.");
    }

    @GetMapping("getbyid")
    public Result getLogsById(@RequestParam("idLog") Long idLog){
        return Result.success(logsService.findById(idLog), "Les informations du log.", "Log details.", "Detalhes do registo.");
    }

    @GetMapping("getbymsisdn")
    public Result getLogsByMsisdn(@RequestParam("msisdn") String msisdn){
        return Result.success(logsService.findByMsisdn(msisdn),
                "Liste des logs pour ce msisdn.",
                "List of logs for this msisdn.",
                "Lista dos registos para este msisdn.");
    }

    @GetMapping("getbyreference")
    public Result getLogsByReference(@RequestParam("reference") String reference){
        return Result.success(logsService.findByReference(reference),
                "Liste des logs pour cette référence.",
                "List of logs for this reference.",
                "Lista dos registos para esta referência.");
    }

    @GetMapping("getbyperiode")
    public Result getLogsByPeriode(
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateFin){
        return Result.success(logsService.findByDtCreatedBetween(dateDebut, dateFin),
                "Liste des logs pour cette période.",
                "List of logs for this period.",
                "Lista dos registos para este período.");
    }

    @PostMapping("/add")
    public Result addLogs(@RequestBody Logs logs){
        return Result.success(logsService.addLogs(logs),
                "Log enregistré avec succès.",
                "Log recorded successfully.",
                "Registo guardado com sucesso.");
    }
}
