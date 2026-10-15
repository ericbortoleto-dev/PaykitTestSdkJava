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

import com.linx.paykit.common.CancelResult;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.TransactionStatus;
import com.linx.paykit.common.features.PreAuthorizationPayment;
import com.linx.paykit.common.parameter.PendingPreParameters;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.R;
import com.linx.paykit.demo.util.LastTransactionHolder;
import com.linx.paykit.demo.util.PaykitBuilder;

import java.math.BigDecimal;

public class ManagePreAuthorizationFragment extends Fragment {

    private Paykit paykit;

    private EditText etPreId;
    private EditText etCaptureAmount;
    private Button btnCapturePreAuth;
    private Button btnCancelPreAuth;

    private LinearLayout layoutResult;
    private TextView tvResultStatus;
    private TextView tvResultMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_pre_authorization, container, false);

        paykit = PaykitBuilder.buildPaykit(requireContext());

        etPreId = view.findViewById(R.id.etPreId);
        etCaptureAmount = view.findViewById(R.id.etCaptureAmount);
        btnCapturePreAuth = view.findViewById(R.id.btnCapturePreAuth);
        btnCancelPreAuth = view.findViewById(R.id.btnCancelPreAuth);

        layoutResult = view.findViewById(R.id.layoutResult);
        tvResultStatus = view.findViewById(R.id.tvResultStatus);
        tvResultMessage = view.findViewById(R.id.tvResultMessage);

        btnCapturePreAuth.setOnClickListener(v -> executeCapture());
        btnCancelPreAuth.setOnClickListener(v -> executeCancel());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String lastTxId = LastTransactionHolder.getLastTransactionId();
        if (lastTxId != null && !lastTxId.isEmpty()) {
            if (etPreId.getText().toString().trim().isEmpty()) {
                etPreId.setText(lastTxId);
            }
        }
    }

    private void executeCapture() {
        String preId = etPreId.getText().toString().trim();
        if (preId.isEmpty()) {
            Toast.makeText(requireContext(), "Informe o ID da pré-autorização (PreId/NSU)", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etCaptureAmount.getText().toString().trim();
        BigDecimal amount = BigDecimal.ZERO;
        if (!amountStr.isEmpty()) {
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
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
            ((PreAuthorizationPayment) paykit).capturePreAuthorization(params, result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showPaymentResult(result);
                        boolean success = result != null && (result.getStatus() == TransactionStatus.APPROVED || result.getStatus() == TransactionStatus.COMPLETED);
                        if (success) {
                            Toast.makeText(requireContext(), "Captura realizada com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            String msg = result != null ? result.getMessage() : "Erro desconhecido";
                            Toast.makeText(requireContext(), "Falha na captura: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(requireContext(), "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeCancel() {
        String preId = etPreId.getText().toString().trim();
        if (preId.isEmpty()) {
            Toast.makeText(requireContext(), "Informe o ID da pré-autorização (PreId/NSU)", Toast.LENGTH_SHORT).show();
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
            ((PreAuthorizationPayment) paykit).cancelPreAuthorization(params, result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showCancelResult(result);
                        boolean success = result != null && result.getStatus() == TransactionStatus.CANCELLED;
                        if (success) {
                            Toast.makeText(requireContext(), "Cancelamento realizado com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            String msg = result != null ? result.getMessage() : "Erro desconhecido";
                            Toast.makeText(requireContext(), "Falha no cancelamento: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(requireContext(), "Paykit não suporta PreAuthorizationPayment", Toast.LENGTH_SHORT).show();
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
