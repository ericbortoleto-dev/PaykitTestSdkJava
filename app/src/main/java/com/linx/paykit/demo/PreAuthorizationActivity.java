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
import com.linx.paykit.common.OrderItem;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.TransactionStatus;
import com.linx.paykit.common.features.PreAuthorizationPayment;
import com.linx.paykit.common.parameter.CreditParameters;
import com.linx.paykit.common.parameter.PendingPreParameters;
import com.linx.paykit.common.parameter.type.CreditTransactionType;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.LastTransactionHolder;
import com.linx.paykit.demo.util.PaykitBuilder;

import java.math.BigDecimal;
import java.util.Collections;

public class PreAuthorizationActivity extends AppCompatActivity {

    private Paykit paykit;

    private EditText etAmount;
    private EditText etExternalId;
    private Button btnPreAuthorize;

    private LinearLayout layoutResult;
    private TextView tvResultStatus;
    private TextView tvResultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_authorization);

        paykit = PaykitBuilder.buildPaykit(this);

        etAmount = findViewById(R.id.etAmount);
        etExternalId = findViewById(R.id.etExternalId);
        btnPreAuthorize = findViewById(R.id.btnPreAuthorize);

        layoutResult = findViewById(R.id.layoutResult);
        tvResultStatus = findViewById(R.id.tvResultStatus);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        btnPreAuthorize.setOnClickListener(v -> executePreAuthorization());

        // Preenche com o ID da última transação se houver
        String lastTxId = com.linx.paykit.demo.util.LastTransactionHolder.getLastTransactionId();
        if (lastTxId != null && !lastTxId.isEmpty()) {
            etExternalId.setText(lastTxId);
        }
    }

    private void executePreAuthorization() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Informe o valor da pré-autorização", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(this, "O valor deve ser maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        String rawExternalId = etExternalId.getText().toString().trim();
        final String externalId = rawExternalId.isEmpty() ? String.valueOf(System.currentTimeMillis()) : rawExternalId;

        CreditParameters params = new CreditParameters(
                amount,
                1,
                null,
                null,
                null,
                externalId,
                Collections.singletonList(new OrderItem("1", "Produto", amount.multiply(BigDecimal.valueOf(100)).longValue(), 1, "UN")),
                false,
                null,
                null,
                CreditTransactionType.PRE_AUTHORIZATION,
                null,
                true,
                false,
                false
        );

        if (paykit instanceof PreAuthorizationPayment) {
            ((PreAuthorizationPayment) paykit).preAuthorize(params, result -> runOnUiThread(() -> {
                showResult(result);
                boolean success = result != null && (result.getStatus() == TransactionStatus.APPROVED || result.getStatus() == TransactionStatus.COMPLETED);
                if (success) {
                    if (result != null && result.getId() != null) {
                        etExternalId.setText(result.getId());
                        LastTransactionHolder.setLastTransaction(result.getId(), externalId);
                    }
                    Toast.makeText(PreAuthorizationActivity.this, "Pré-autorização aprovada com sucesso!", Toast.LENGTH_LONG).show();
                } else {
                    String msg = result != null ? result.getMessage() : "Erro desconhecido";
                    Toast.makeText(PreAuthorizationActivity.this, "Pré-autorização não aprovada: " + msg, Toast.LENGTH_LONG).show();
                }
            }));
        } else {
            Toast.makeText(this, "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult(PaymentResult result) {
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
}
