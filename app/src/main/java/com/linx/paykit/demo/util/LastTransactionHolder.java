package com.linx.paykit.demo.util;

public class LastTransactionHolder {
    private static String lastTransactionId = null;
    private static String lastExternalId = null;

    public static void setLastTransaction(String transactionId, String externalId) {
        lastTransactionId = transactionId;
        lastExternalId = externalId;
    }

    public static String getLastTransactionId() {
        return lastTransactionId;
    }

    public static String getLastExternalId() {
        return lastExternalId;
    }
}
