package com.api.regie.dto;

/**
 * Données utiles extraites d'un retour Soutra, qu'il provienne du callback ou de la consultation
 * de statut.
 *
 * @param uniqueTxnId référence que nous avons transmise à Soutra ; correspond à {@code Paiements.reference}
 * @param txnId       identifiant de la transaction chez Soutra
 * @param txnStatus   code de statut, voir {@link com.api.regie.utils.SoutraTxnStatus}
 * @param phoneNumber numéro du payeur
 * @param username    nom du payeur tel que connu de Soutra
 * @param message     libellé technique renvoyé par Soutra
 */
public record SoutraTransactionInfo(
        String uniqueTxnId,
        String txnId,
        Integer txnStatus,
        String phoneNumber,
        String username,
        String message
) {}
