package com.linx.paykit.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.linx.paykit.common.OrderItem;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.TransactionStatus;
import com.linx.paykit.common.features.PreAuthorizationPayment;
import com.linx.paykit.common.parameter.CreditParameters;
import com.linx.paykit.common.parameter.type.CreditTransactionType;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.R;
import com.linx.paykit.demo.util.LastTransactionHolder;
import com.linx.paykit.demo.util.PaykitBuilder;

import java.math.BigDecimal;
import java.util.Collections;

public class NewPreAuthorizationFragment extends Fragment {

    private Paykit paykit;

    private EditText etAmount;
    private EditText etExternalId;
    private Button btnPreAuthorize;

    private LinearLayout layoutResult;
    private TextView tvResultStatus;
    private TextView tvResultMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_pre_authorization, container, false);

        paykit = PaykitBuilder.buildPaykit(requireContext());

        etAmount = view.findViewById(R.id.etAmount);
        etExternalId = view.findViewById(R.id.etExternalId);
        btnPreAuthorize = view.findViewById(R.id.btnPreAuthorize);

        layoutResult = view.findViewById(R.id.layoutResult);
        tvResultStatus = view.findViewById(R.id.tvResultStatus);
        tvResultMessage = view.findViewById(R.id.tvResultMessage);

        btnPreAuthorize.setOnClickListener(v -> executePreAuthorization());

        String lastTxId = LastTransactionHolder.getLastTransactionId();
        if (lastTxId != null && !lastTxId.isEmpty()) {
            etExternalId.setText(lastTxId);
        }

        return view;
    }

    private void executePreAuthorization() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Informe o valor da pré-autorização", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(requireContext(), "O valor deve ser maior que zero", Toast.LENGTH_SHORT).show();
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
            ((PreAuthorizationPayment) paykit).preAuthorize(params, result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showResult(result);
                        boolean success = result != null && (result.getStatus() == TransactionStatus.APPROVED || result.getStatus() == TransactionStatus.COMPLETED);
                        if (success) {
                            if (result != null && result.getId() != null) {
                                etExternalId.setText(result.getId());
                                LastTransactionHolder.setLastTransaction(result.getId(), externalId);
                            }
                            Toast.makeText(requireContext(), "Pré-autorização aprovada com sucesso!", Toast.LENGTH_LONG).show();

                            if (getActivity() instanceof PreAuthorizationActivity) {
                                ((PreAuthorizationActivity) getActivity()).switchToManageTab();
                            }
                        } else {
                            String msg = result != null ? result.getMessage() : "Erro desconhecido";
                            Toast.makeText(requireContext(), "Pré-autorização não aprovada: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(requireContext(), "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
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
