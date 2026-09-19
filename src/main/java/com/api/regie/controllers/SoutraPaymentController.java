package com.api.regie.controllers;

import com.api.regie.dto.SoutraCheckStatusRequestDTO;
import com.api.regie.dto.SoutraInitiatePaymentRequestDTO;
import com.api.regie.models.Result;
import com.api.regie.models.SoutraConfig;
import com.api.regie.services.SoutraTransfertService;
import com.api.regie.utils.SignatureEncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/soutra-payments")
public class SoutraPaymentController {

    private static final Logger log = LoggerFactory.getLogger(SoutraPaymentController.class);

    private final SoutraTransfertService soutraTransfertService;

    public SoutraPaymentController(SoutraTransfertService soutraTransfertService) {
        this.soutraTransfertService = soutraTransfertService;
    }

    @PostMapping("/test-soutra-check-status")
    public Result<?> testSoutraCheckStatus(@RequestBody SoutraCheckStatusRequestDTO request) {
        try {
            return Result.success(soutraTransfertService.checkStatus(request.reference()),
                    "Statut récupéré.", "Status retrieved.", "Estado obtido.");
        } catch (Exception ex) {
            log.error("Soutra check status test error: {}", ex.getMessage(), ex);
            return Result.error(500,
                    "Erreur vérification statut : " + ex.getMessage(),
                    "Status check error: " + ex.getMessage(),
                    "Erro ao verificar o estado: " + ex.getMessage());
        }
    }

    @PostMapping("/test-soutra-initiate-payment")
    public Result<?> testSoutraInitiatePayment(@RequestBody SoutraInitiatePaymentRequestDTO request) {
        try {
            var response = soutraTransfertService.initiatePayment(
                request.amount(), request.note(), request.reference());
            return Result.success(response, "Paiement initié.", "Payment initiated.", "Pagamento iniciado.");
        } catch (Exception ex) {
            log.error("Soutra initiate payment test error: {}", ex.getMessage(), ex);
            return Result.error(500,
                    "Erreur initiation paiement : " + ex.getMessage(),
                    "Payment initiation error: " + ex.getMessage(),
                    "Erro ao iniciar o pagamento: " + ex.getMessage());
        }
    }

    @GetMapping("/testSoutraApiCallback")
    @io.swagger.v3.oas.annotations.Operation(summary = "test api soutra callback")
    public Result<?> testSoutraApiCallback(@RequestParam(name = "data",required = false) String data,
                                                     @RequestParam(name = "merchant_mobile_no",required = false) String merchant_mobile_no) {
        try {

            if (data==null || data.isEmpty()) {
                  return Result.error(500,
                        "Erreur vérification data : ",
                        "Data check error: ",
                        "Erro ao verificar o edata: ");
            }

            SoutraConfig config = soutraTransfertService.activeConfig();
            log.info("Soutra api callback number: {}", merchant_mobile_no);
            log.info("Soutra api callback decrypt: {}", SignatureEncryptionUtils.decryptData(data,config.getClientSecret()));


            return Result.success(null, "Callback initié.", "Callback initiated.", "Callbacko iniciado.");

        } catch (Exception e) {
            return Result.error(500,
                    "Erreur Callback paiement : " + e.getMessage(),
                    "Callback initiation error: " + e.getMessage(),
                    "Erro ao callbacko o pagamento: " + e.getMessage());
        }

    }
}
