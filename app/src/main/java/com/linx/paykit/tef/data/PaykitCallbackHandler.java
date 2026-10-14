package com.linx.paykit.tef.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.linx.paykit.common.ui.ConfirmationOperationResult;
import com.linx.paykit.common.ui.InputOperationResult;
import com.linx.paykit.common.ui.InputParameters;
import com.linx.paykit.common.ui.MenuOperationResult;
import com.linx.paykit.common.ui.PaykitUiCallback;
import com.linx.paykit.tef.domain.PaykitUiInteractionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PaykitCallbackHandler implements PaykitUiCallback {

    private static final String TAG = "PaykitCallbackHandler";
    private boolean isCanceled = false;
    private final PaykitUiInteractionListener uiListener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public PaykitCallbackHandler(PaykitUiInteractionListener uiListener) {
        this.uiListener = uiListener;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void onShowAlert(String message) {
        Log.d(TAG, "onShowAlert: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestAlertDisplay(message));
        }
    }

    @Override
    public void onShowMenu(String label, String[] items, Function1<? super MenuOperationResult, Unit> operationResult) {
        onShowMenu(label, items, null, operationResult);
    }

    public void onShowMenu(String label, String[] items, Integer timeout, Function1<? super MenuOperationResult, Unit> operationResult) {
        Log.d(TAG, "onShowMenu: " + label);
        if (uiListener != null) {
            mainHandler.post(() -> {
                List<String> itemList = items != null ? Arrays.asList(items) : Collections.emptyList();
                uiListener.requestMenuDisplay(label, itemList, result -> {
                    if (operationResult != null) {
                        operationResult.invoke(result);
                    }
                });
            });
        }
    }

    @Override
    public void onShowError(String message) {
        Log.d(TAG, "onShowError: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestErrorDisplay(message));
        }
    }

    @Override
    public void onShowMessage(String message) {
        Log.d(TAG, "onShowMessage: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestMessageDisplay(message));
        }
    }

    @Override
    public void onShowInsertCard(String message) {
        Log.d(TAG, "onShowInsertCard: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestCardInstructionDisplay(message, PaykitCallbackState.CardInstructionType.INSERT));
        }
    }

    @Override
    public void onShowProcessing(String message) {
        Log.d(TAG, "onShowProcessing: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestProcessingDisplay(message));
        }
    }

    @Override
    public void onShowRemoveCard(String message) {
        Log.d(TAG, "onShowRemoveCard: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestCardInstructionDisplay(message, PaykitCallbackState.CardInstructionType.REMOVE));
        }
    }

    @Override
    public void onShowConfirmation(String message, Function1<? super ConfirmationOperationResult, Unit> operationResult) {
        Log.d(TAG, "onShowConfirmation: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> {
                uiListener.requestConfirmationDisplay(message, result -> {
                    if (operationResult != null) {
                        operationResult.invoke(result);
                    }
                });
            });
        }
    }

    @Override
    public void onShowInput(InputParameters parameters, Function1<? super InputOperationResult, Unit> operationResult) {
        Log.d(TAG, "onShowInput: " + (parameters != null ? parameters.getLabel() : ""));
        if (uiListener != null) {
            mainHandler.post(() -> {
                uiListener.requestInputDisplay(parameters, result -> {
                    if (operationResult != null) {
                        operationResult.invoke(result);
                    }
                });
            });
        }
    }

    @Override
    public void onShowQrCode(String message, String qrCode, int timeout) {
        Log.d(TAG, "onShowQrCode: " + message);
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestQrCodeDisplay(message, qrCode, timeout));
        }
    }

    @Override
    public void onShowPassword() {
        Log.d(TAG, "onShowPassword");
        if (uiListener != null) {
            mainHandler.post(() -> uiListener.requestPasswordDisplay());
        }
    }

    @Override
    public void setCanceledStatus(boolean canceled) {
        Log.d(TAG, "setCanceledStatus: " + canceled);
        this.isCanceled = canceled;
    }

    @Override
    public boolean showCanceledStatus() {
        return isCanceled;
    }
}
