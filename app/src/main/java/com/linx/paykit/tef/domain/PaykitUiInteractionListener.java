package com.linx.paykit.tef.domain;

import com.linx.paykit.common.ui.ConfirmationOperationResult;
import com.linx.paykit.common.ui.InputOperationResult;
import com.linx.paykit.common.ui.InputParameters;
import com.linx.paykit.common.ui.MenuOperationResult;
import com.linx.paykit.tef.data.PaykitCallbackState;

import java.util.List;
import java.util.function.Consumer;

public interface PaykitUiInteractionListener {
    void requestAlertDisplay(String message);
    void requestMenuDisplay(String label, List<String> items, Consumer<MenuOperationResult> onResult);
    void requestErrorDisplay(String message);
    void requestMessageDisplay(String message);
    void requestCardInstructionDisplay(String message, PaykitCallbackState.CardInstructionType type);
    void requestProcessingDisplay(String message);
    void requestConfirmationDisplay(String message, Consumer<ConfirmationOperationResult> onResult);
    void requestInputDisplay(InputParameters parameters, Consumer<InputOperationResult> onResult);
    void requestQrCodeDisplay(String message, String qrCodeData, int timeout);
    void requestPasswordDisplay();
}
