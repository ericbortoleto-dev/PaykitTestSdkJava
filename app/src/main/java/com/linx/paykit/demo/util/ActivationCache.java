package com.linx.paykit.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.linx.paykit.common.activation.ActivationParameters;

public class ActivationCache {

    private static final String ACTIVATION_PREFS_NAME = "activation_cache";
    private static final String KEY_IS_ACTIVATED = "is_activated";
    private static final String KEY_ACTIVATION_FINGERPRINT = "activation_fingerprint";

    public static void saveActivationState(Context context, ActivationParameters params) {
        String fingerprint = buildActivationFingerprint(params);
        if (fingerprint == null) return;

        SharedPreferences prefs = context.getSharedPreferences(ACTIVATION_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_IS_ACTIVATED, true)
                .putString(KEY_ACTIVATION_FINGERPRINT, fingerprint)
                .apply();
    }

    public static boolean loadActivationState(Context context, ActivationParameters params) {
        String currentFingerprint = buildActivationFingerprint(params);
        if (currentFingerprint == null) return false;

        SharedPreferences prefs = context.getSharedPreferences(ACTIVATION_PREFS_NAME, Context.MODE_PRIVATE);
        boolean isActivated = prefs.getBoolean(KEY_IS_ACTIVATED, false);
        String cachedFingerprint = prefs.getString(KEY_ACTIVATION_FINGERPRINT, null);

        return isActivated && currentFingerprint.equals(cachedFingerprint);
    }

    public static void clearActivationState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ACTIVATION_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_IS_ACTIVATED)
                .remove(KEY_ACTIVATION_FINGERPRINT)
                .apply();
    }

    private static String buildActivationFingerprint(ActivationParameters params) {
        if (params == null) return null;

        String storeCnpj = params.getStoreCnpj() != null ? params.getStoreCnpj() : "";
        String tipoServidor = params.getTipoServidor() != null ? params.getTipoServidor().name() : "";
        String tefProduction = params.getTef() != null ? String.valueOf(params.getTef().getProduction()) : "";
        String pagSeguroCode = params.getPagSeguro() != null ? params.getPagSeguro().getActivationCode() : "";
        String sitefCompanyCode = params.getSitef() != null ? params.getSitef().getSitefCompanyCode() : "";

        return storeCnpj + "|" +
                tipoServidor + "|" +
                tefProduction + "|" +
                pagSeguroCode + "|" +
                sitefCompanyCode;
    }
}
