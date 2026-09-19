package com.api.regie.services;

import com.api.regie.dto.AuthResponseSoutra;
import com.api.regie.dto.SoutraInitiateResponse;
import com.api.regie.dto.SoutraTransactionInfo;
import com.api.regie.dto.TransfertResponseSoutra;
import com.api.regie.models.SoutraConfig;
import com.api.regie.repository.SoutraConfigRepository;
import com.api.regie.utils.SignatureEncryptionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class SoutraTransfertService {

    private static final Logger log = LoggerFactory.getLogger(SoutraTransfertService.class);

    private final SoutraConfigRepository soutraConfigRepository;
    private final ObjectMapper objectMapper;

    public SoutraTransfertService(SoutraConfigRepository soutraConfigRepository, ObjectMapper objectMapper) {
        this.soutraConfigRepository = soutraConfigRepository;
        this.objectMapper = objectMapper;
    }

    public SoutraConfig activeConfig() {
        return soutraConfigRepository.findByActifTrue()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucune configuration Soutra active en base de données"));
    }

    private RestClient buildRestClient(SoutraConfig config) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(30_000);

        return RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    private AuthResponseSoutra authenticate(SoutraConfig config, RestClient restClient) {
        String basicAuth = "Basic " + SignatureEncryptionUtils
                .generateSignatureAuthentification(config.getClientId(), config.getClientSecret());
        log.info("SoutraTransfert authenticate [environnement={}, clientId={}]",
                config.getEnvironnement(), config.getClientId());
        String raw = restClient.post()
                .uri(uri -> uri.path("/digipay/v3/open_api/authenticate")
                        .queryParam("client_id", config.getClientId())
                        .queryParam("client_secret", config.getClientSecret())
                        .build())
                .header("Authorization", basicAuth)
                .header("User-Agent", "insomnia/11.3.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        log.debug("Authenticate raw response: {}", raw);
        try {
            return objectMapper.readValue(raw, AuthResponseSoutra.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse authenticate response: " + raw, ex);
        }
    }

    public TransfertResponseSoutra checkStatus(String reference) {
        SoutraConfig config = activeConfig();
        RestClient restClient = buildRestClient(config);
        AuthResponseSoutra auth = authenticate(config, restClient);
        if (auth == null || auth.getData() == null) {
            throw new IllegalStateException("Authentication failed");
        }
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("unique_txn_id", reference);
        bodyMap.put("txn_id", null);
        String bodyJson;
        try {
            bodyJson = objectMapper.writeValueAsString(bodyMap);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize status request", ex);
        }
        try {
            String encrypted = SignatureEncryptionUtils.encryptData(bodyJson, config.getClientSecret());
            String signature = SignatureEncryptionUtils.generateSignatureBody(bodyJson, config.getApiKey());
            String raw = restClient.get()
                    .uri("/digipay/v3/open_api/transaction_detail?requestString={enc}", encrypted)
                    .header("Authorization", "Bearer " + auth.getData().getAccessToken())
                    .header("CompanyId", auth.getData().getCompanyId())
                    .header("Signature", signature)
                    .retrieve()
                    .body(String.class);
            return parseTransferResponse(raw, config.getClientSecret());
        } catch (Exception ex) {
            log.error("SoutraTransfert checkStatus error: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Check status failed: " + ex.getMessage(), ex);
        }
    }

    public SoutraInitiateResponse initiatePayment(
         double amount, String note, String reference) {
        SoutraConfig config = activeConfig();
        RestClient restClient = buildRestClient(config);
        AuthResponseSoutra auth = authenticate(config, restClient);
        if (auth == null || auth.getData() == null) {
            throw new IllegalStateException("Authentication failed");
        }
        Map<String, Object> payeeInfo = new HashMap<>();
        payeeInfo.put("dial_code", "+224");
        payeeInfo.put("phone_number", config.getMsisdnMarchand());
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("amount", amount);
        bodyMap.put("note", note);
        bodyMap.put("payee_information", payeeInfo);
        bodyMap.put("redirect_url", config.getRedirectUrl());
        bodyMap.put("unique_txn_id", reference);
        log.info("SoutraTransfert initiate payment request: {}", bodyMap);
        String bodyJson;
        try {
            bodyJson = objectMapper.writeValueAsString(bodyMap);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize initiate request", ex);
        }

        log.info("SoutraTransfert initiate payment request json: {}", bodyJson);
        try {
            String encrypted = SignatureEncryptionUtils.encryptData(bodyJson, config.getClientSecret());
            log.info("SoutraTransfert initiate payment request encrypt: {}", encrypted);
            String signature = SignatureEncryptionUtils.generateSignatureBody(bodyJson, config.getApiKey());
            log.info("SoutraTransfert initiate payment signature encrypt: {}", signature);
            String raw = restClient.post()
                    .uri("/digipay/v3/open_api/initiate_payment")
                    .header("Authorization", "Bearer " + auth.getData().getAccessToken())
                    .header("CompanyId", auth.getData().getCompanyId())
                    .header("Signature", signature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(encrypted)
                    .retrieve()
                    .body(String.class);
            log.debug("initiatePayment raw response: {}", raw);
            try {
                String decrypted = SignatureEncryptionUtils.decryptData(raw, config.getClientSecret());
                return objectMapper.readValue(decrypted, SoutraInitiateResponse.class);
            } catch (Exception ignored) {
                return objectMapper.readValue(raw, SoutraInitiateResponse.class);
            }
        } catch (Exception ex) {
            log.error("SoutraTransfert initiatePayment error: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Initiate payment failed: " + ex.getMessage(), ex);
        }
    }

    private TransfertResponseSoutra parseTransferResponse(String raw, String secretKey) {
        if (raw == null) return new TransfertResponseSoutra();
        try {
            TransfertResponseSoutra response = objectMapper.readValue(raw, TransfertResponseSoutra.class);
            if (response.getData() != null && !response.getData().isBlank()) {
                try {
                    String decrypted = SignatureEncryptionUtils.decryptData(response.getData(), secretKey);
                    log.info("TransferMoney decrypted: {}", decrypted);
                    response.setDecryptedData(objectMapper.readTree(decrypted));
                } catch (Exception decryptEx) {
                    log.warn("Could not decrypt transfer response data: {}", decryptEx.getMessage());
                }
            }
            return response;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse transfer response", ex);
        }
    }

    // ------------------------------------------------------------------ lecture des retours Soutra

    /**
     * Déchiffre puis interprète la charge utile reçue sur le callback.
     *
     * <p>Le déchiffrement tient lieu d'authentification : la route du callback est publique, mais
     * un appelant ne détenant pas le {@code clientSecret} ne peut pas produire de charge exploitable.</p>
     */
    public SoutraTransactionInfo parseCallback(String data) {
        SoutraConfig config = activeConfig();
        try {
            String decrypted = SignatureEncryptionUtils.decryptData(data, config.getClientSecret());
            log.info("Soutra callback déchiffré : {}", decrypted);

            return parseTransactionInfo(objectMapper.readTree(decrypted));
        } catch (Exception ex) {
            throw new IllegalStateException("Callback Soutra illisible : " + ex.getMessage(), ex);
        }
    }

    /** Interroge Soutra sur une transaction et en extrait les données utiles. */
    public SoutraTransactionInfo statusOf(String reference) {
        TransfertResponseSoutra response = checkStatus(reference);

        if (response == null || response.getDecryptedData() == null) {
            throw new IllegalStateException("Statut Soutra indisponible pour la référence " + reference);
        }

        return parseTransactionInfo(response.getDecryptedData());
    }

    /**
     * Extraction tolérante des champs d'une transaction.
     *
     * <p>Le callback imbrique la transaction sous {@code transaction.transaction} ; la consultation
     * de statut ne présente pas nécessairement le même niveau d'imbrication. On descend donc au
     * niveau qui porte réellement {@code unique_txn_id}.</p>
     */
    private SoutraTransactionInfo parseTransactionInfo(JsonNode racine) {
        if (racine == null) {
            throw new IllegalStateException("Retour Soutra vide");
        }

        JsonNode enveloppe = racine.has("transaction") ? racine.get("transaction") : racine;
        JsonNode transaction = enveloppe.has("transaction") ? enveloppe.get("transaction") : enveloppe;

        // Si le niveau retenu ne porte pas la référence, revenir à la racine plutôt que rendre du vide.
        if (!transaction.has("unique_txn_id") && racine.has("unique_txn_id")) {
            transaction = racine;
        }

        JsonNode client = racine.has("customer_data") ? racine.get("customer_data") : null;

        return new SoutraTransactionInfo(
                texte(transaction, "unique_txn_id"),
                texte(transaction, "txn_id"),
                entier(transaction, "txn_status"),
                client == null ? null : texte(client, "phone_number"),
                client == null ? null : texte(client, "username"),
                texte(enveloppe, "message"));
    }

    private String texte(JsonNode noeud, String champ) {
        JsonNode valeur = noeud == null ? null : noeud.get(champ);

        return valeur == null || valeur.isNull() ? null : valeur.asText();
    }

    private Integer entier(JsonNode noeud, String champ) {
        JsonNode valeur = noeud == null ? null : noeud.get(champ);

        return valeur == null || valeur.isNull() ? null : valeur.asInt();
    }
}
