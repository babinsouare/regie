package com.api.regie.utils;

/**
 * Codes de statut d'une transaction Soutra, tels que portés par {@code txn_status}.
 *
 * <p>Seul {@link #SUCCESS} vaut encaissement. {@link #PENDING} et {@link #APPROVED} sont des états
 * transitoires : la transaction peut encore évoluer et sera réinterrogée. Les autres sont
 * terminaux et sans encaissement.</p>
 */
public interface SoutraTxnStatus {

    int PENDING = 1;
    int APPROVED = 2;
    int SUCCESS = 3;
    int FAILED = 4;
    int REJECTED = 5;
    int REFUNDED = 6;

    /** Vrai tant que la transaction n'a pas atteint un état définitif. */
    static boolean estTransitoire(Integer txnStatus) {
        return txnStatus == null || txnStatus == PENDING || txnStatus == APPROVED;
    }

    /** Statut de paiement OGP correspondant à un code Soutra. */
    static String statutPaiement(Integer txnStatus) {
        if (txnStatus == null) return "pending";

        return switch (txnStatus) {
            case SUCCESS -> "success";
            case REFUNDED -> "refunded";
            case FAILED, REJECTED -> "failed";
            default -> "pending";
        };
    }
}
