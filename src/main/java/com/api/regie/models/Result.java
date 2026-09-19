package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enveloppe de réponse de l'API.
 *
 * <p>Chaque réponse porte le message dans les trois langues supportées ; le front choisit
 * celui qu'il affiche selon la locale de l'utilisateur.</p>
 *
 * <pre>
 * {
 *   "status": 404,
 *   "message": "Campagne introuvable.",
 *   "message_en": "Campaign not found.",
 *   "message_pt": "Campanha não encontrada.",
 *   "data": null
 * }
 * </pre>
 *
 * <p>Les trois messages sont obligatoires : il n'existe pas de fabrique monolingue, une
 * réponse non traduite ne peut donc pas compiler.</p>
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonPropertyOrder({"status", "message", "message_en", "message_pt", "data"})
public class Result<T> {

    private int status;

    /** Message en français. */
    private String message;

    /** Message en anglais. */
    @JsonProperty("message_en")
    private String messageEn;

    /** Message en portugais. */
    @JsonProperty("message_pt")
    private String messagePt;

    private T data;

    /**
     * Réponse 200.
     *
     * @param data      charge utile renvoyée au client
     * @param message   message en français
     * @param messageEn message en anglais
     * @param messagePt message en portugais
     */
    public static <T> Result<T> success(T data, String message, String messageEn, String messagePt) {
        return new Result<>(200, message, messageEn, messagePt, data);
    }

    /**
     * Réponse d'erreur, sans charge utile.
     *
     * @param status    code porté par le corps de la réponse
     * @param message   message en français
     * @param messageEn message en anglais
     * @param messagePt message en portugais
     */
    public static <T> Result<T> error(int status, String message, String messageEn, String messagePt) {
        return new Result<>(status, message, messageEn, messagePt, null);
    }

}
