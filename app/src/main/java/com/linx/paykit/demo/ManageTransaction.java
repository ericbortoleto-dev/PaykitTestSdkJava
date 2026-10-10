package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linx.paykit.common.CancelResult;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.TransactionStatus;
import com.linx.paykit.common.features.PreAuthorizationPayment;
import com.linx.paykit.common.parameter.PendingPreParameters;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.LastTransactionHolder;
import com.linx.paykit.demo.util.PaykitBuilder;

import java.math.BigDecimal;

public class ManageTransaction extends AppCompatActivity {

    private Paykit paykit;

    private EditText etPreId;
    private EditText etCaptureAmount;
    private Button btnCapturePreAuth;
    private Button btnCancelPreAuth;

    private LinearLayout layoutResult;
    private TextView tvResultStatus;
    private TextView tvResultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transaction);

        paykit = PaykitBuilder.buildPaykit(this);

        etPreId = findViewById(R.id.etPreId);
        etCaptureAmount = findViewById(R.id.etCaptureAmount);
        btnCapturePreAuth = findViewById(R.id.btnCapturePreAuth);
        btnCancelPreAuth = findViewById(R.id.btnCancelPreAuth);

        layoutResult = findViewById(R.id.layoutResult);
        tvResultStatus = findViewById(R.id.tvResultStatus);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        btnCapturePreAuth.setOnClickListener(v -> executeCapture());
        btnCancelPreAuth.setOnClickListener(v -> executeCancel());

        // Preenche com o ID da última transação se houver
        String lastTxId = LastTransactionHolder.getLastTransactionId();
        if (lastTxId != null && !lastTxId.isEmpty()) {
            etPreId.setText(lastTxId);
        }
    }

    private void executeCapture() {
        String preId = etPreId.getText().toString().trim();
        if (preId.isEmpty()) {
            Toast.makeText(this, "Informe o ID da pré-autorização (PreId/NSU)", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etCaptureAmount.getText().toString().trim();
        BigDecimal amount = BigDecimal.ZERO;
        if (!amountStr.isEmpty()) {
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        PendingPreParameters params = new PendingPreParameters(
                preId,
                amount,
                null,
                false,
                false,
                true,
                true
        );

        if (paykit instanceof PreAuthorizationPayment) {
            ((PreAuthorizationPayment) paykit).capturePreAuthorization(params, result -> runOnUiThread(() -> {
                showPaymentResult(result);
                boolean success = result != null && (result.getStatus() == TransactionStatus.APPROVED || result.getStatus() == TransactionStatus.COMPLETED);
                if (success) {
                    Toast.makeText(ManageTransaction.this, "Captura realizada com sucesso!", Toast.LENGTH_LONG).show();
                } else {
                    String msg = result != null ? result.getMessage() : "Erro desconhecido";
                    Toast.makeText(ManageTransaction.this, "Falha na captura: " + msg, Toast.LENGTH_LONG).show();
                }
            }));
        } else {
            Toast.makeText(this, "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeCancel() {
        String preId = etPreId.getText().toString().trim();
        if (preId.isEmpty()) {
            Toast.makeText(this, "Informe o ID da pré-autorização (PreId/NSU)", Toast.LENGTH_SHORT).show();
            return;
        }

        PendingPreParameters params = new PendingPreParameters(
                preId,
                null,
                null,
                false,
                false,
                true,
                true
        );

        if (paykit instanceof PreAuthorizationPayment) {
            ((PreAuthorizationPayment) paykit).cancelPreAuthorization(params, result -> runOnUiThread(() -> {
                showCancelResult(result);
                boolean success = result != null && result.getStatus() == TransactionStatus.CANCELLED;
                if (success) {
                    Toast.makeText(ManageTransaction.this, "Cancelamento realizado com sucesso!", Toast.LENGTH_LONG).show();
                } else {
                    String msg = result != null ? result.getMessage() : "Erro desconhecido";
                    Toast.makeText(ManageTransaction.this, "Falha no cancelamento: " + msg, Toast.LENGTH_LONG).show();
                }
            }));
        } else {
            Toast.makeText(this, "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentResult(PaymentResult result) {
        layoutResult.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        if (result != null) {
            sb.append("Status: ").append(result.getStatus());
            if (result.getId() != null) sb.append("\nID: ").append(result.getId());
            if (result.getMessage() != null) sb.append("\nMessage: ").append(result.getMessage());
            if (result.getRawData() != null) sb.append("\nData: ").append(result.getRawData());
        } else {
            sb.append("Sem resposta");
        }
        tvResultStatus.setText(sb.toString());
        tvResultMessage.setVisibility(View.GONE);
    }

    private void showCancelResult(CancelResult result) {
        layoutResult.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        if (result != null) {
            sb.append("Status: ").append(result.getStatus());
            if (result.getId() != null) sb.append("\nID: ").append(result.getId());
            if (result.getMessage() != null) sb.append("\nMessage: ").append(result.getMessage());
        } else {
            sb.append("Sem resposta");
        }
        tvResultStatus.setText(sb.toString());
        tvResultMessage.setVisibility(View.GONE);
    }
}
