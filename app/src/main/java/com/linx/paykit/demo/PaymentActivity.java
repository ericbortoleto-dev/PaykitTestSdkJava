package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linx.paykit.common.Callback;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.TransactionStatus;
import com.linx.paykit.common.parameter.CreditParameters;
import com.linx.paykit.common.parameter.DebitParameters;
import com.linx.paykit.common.parameter.PaymentParameters;
import com.linx.paykit.common.parameter.VoucherParameters;
import com.linx.paykit.common.parameter.type.CreditTransactionType;
import com.linx.paykit.common.parameter.type.DebitTransactionType;
import com.linx.paykit.common.parameter.type.VoucherTransactionType;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.PaykitBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PaymentActivity extends AppCompatActivity {

    private Paykit paykit;

    private EditText etAmount;
    private EditText etExternalId;
    private CheckBox cbAutoConfirm;
    private CheckBox cbAutoPrint;
    private CheckBox cbMerchantReceipt;
    private RadioGroup rgPaymentType;
    private EditText etInstallments;
    private EditText etPostCreditDays;
    private Button btnStartPayment;

    private LinearLayout layoutPaymentResult;
    private TextView tvPaymentResultStatus;
    private TextView tvPaymentResultMessage;
    private EditText etJsonQR;
    private EditText etJsonQuery;
    private Button btnGenericPayment;
    private Button btnQueryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paykit = PaykitBuilder.buildPaykit(this);

        etAmount = findViewById(R.id.etAmount);
        etExternalId = findViewById(R.id.etExternalId);
        cbAutoConfirm = findViewById(R.id.cbAutoConfirm);
        cbAutoPrint = findViewById(R.id.cbAutoPrint);
        cbMerchantReceipt = findViewById(R.id.cbMerchantReceipt);
        rgPaymentType = findViewById(R.id.rgPaymentType);
        etInstallments = findViewById(R.id.etInstallments);
        etPostCreditDays = findViewById(R.id.etPostCreditDays);
        btnStartPayment = findViewById(R.id.btnStartPayment);
        etJsonQR = findViewById(R.id.etJsonQR);
        etJsonQuery = findViewById(R.id.etJsonQuery);
        btnGenericPayment = findViewById(R.id.btnGenericPayment);
        btnQueryReport = findViewById(R.id.btnQueryReport);

        layoutPaymentResult = findViewById(R.id.layoutPaymentResult);
        tvPaymentResultStatus = findViewById(R.id.tvPaymentResultStatus);
        tvPaymentResultMessage = findViewById(R.id.tvPaymentResultMessage);

        layoutPaymentResult = findViewById(R.id.layoutPaymentResult);
        tvPaymentResultStatus = findViewById(R.id.tvPaymentResultStatus);
        tvPaymentResultMessage = findViewById(R.id.tvPaymentResultMessage);

        btnStartPayment.setOnClickListener(v -> executePayment());
        btnGenericPayment.setOnClickListener(v -> executeGenericPayment());
        btnQueryReport.setOnClickListener(v -> executeQueryReport());
    }

    private void executePayment() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Informe o valor do pagamento", Toast.LENGTH_SHORT).show();
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

        String externalId = etExternalId.getText().toString().trim();
        if (externalId.isEmpty()) {
            externalId = String.valueOf(System.currentTimeMillis());
        }

        boolean autoConfirm = cbAutoConfirm.isChecked();
        boolean autoPrint = cbAutoPrint.isChecked();
        boolean merchantReceipt = cbMerchantReceipt.isChecked();

        int installments = 1;
        String instStr = etInstallments.getText().toString().trim();
        if (!instStr.isEmpty()) {
            try {
                installments = Integer.parseInt(instStr);
            } catch (NumberFormatException ignored) {}
        }

        int postCreditDays = 0;
        String postDaysStr = etPostCreditDays.getText().toString().trim();
        if (!postDaysStr.isEmpty()) {
            try {
                postCreditDays = Integer.parseInt(postDaysStr);
            } catch (NumberFormatException ignored) {}
        }

        int checkedRadioButtonId = rgPaymentType.getCheckedRadioButtonId();

        Callback<PaymentResult> callback = result -> runOnUiThread(() -> {
            showPaymentResult(result);
            boolean success = result != null && (result.getStatus() == TransactionStatus.COMPLETED || result.getStatus() == TransactionStatus.APPROVED);
            if (success) {
                Toast.makeText(PaymentActivity.this, "Pagamento aprovado com sucesso!", Toast.LENGTH_LONG).show();
            }else {
                String msg = result != null ? result.getMessage() : "Erro desconhecido";
                Toast.makeText(PaymentActivity.this, "Pagamento não aprovado: " + msg, Toast.LENGTH_LONG).show();
            }
        });

        if (checkedRadioButtonId == R.id.rbDebit) {
            DebitParameters params = new DebitParameters(
                    amount,
                    installments > 0 ? installments : null,
                    null,
                    null,
                    null,
                    externalId,
                    null,
                    autoPrint,
                    null,
                    null,
                    null,
                    postCreditDays > 0 ? postCreditDays : null,
                    DebitTransactionType.AT_SIGHT,
                    null,
                    autoConfirm,
                    merchantReceipt
            );
            paykit.debit(params, callback);

        }
        else if (checkedRadioButtonId == R.id.rbCredit) {
            CreditTransactionType creditType = installments > 1 ? CreditTransactionType.STORE_INSTALMENTS : CreditTransactionType.AT_SIGHT;
            CreditParameters params = new CreditParameters(
                    amount,
                    installments > 0 ? installments : null,
                    null,
                    null,
                    null,
                    externalId,
                    null,
                    autoPrint,
                    null,
                    postCreditDays > 0 ? postCreditDays : null,
                    creditType,
                    null,
                    autoConfirm,
                    merchantReceipt
            );
            paykit.credit(params, callback);

        }
        else if (checkedRadioButtonId == R.id.rbPix) {
            PaymentParameters params = new PaymentParameters(
                    amount,
                    null,
                    null,
                    null,
                    null,
                    externalId,
                    null,
                    autoPrint,
                    null,
                    null,
                    autoConfirm,
                    merchantReceipt,
                    null
            );
            paykit.pix(params, callback);

        }
        else if (checkedRadioButtonId == R.id.rbVoucher) {
            VoucherParameters params = new VoucherParameters(
                    amount,
                    null,
                    null,
                    null,
                    null,
                    externalId,
                    null,
                    autoPrint,
                    null,
                    VoucherTransactionType.FOOD,
                    null,
                    autoConfirm,
                    merchantReceipt
            );
            paykit.voucher(params, callback);
        }
        else {
            Toast.makeText(this, "Selecione um tipo de pagamento", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeGenericPayment() {
        BigDecimal amount = parseAmount(etAmount.getText().toString());
        if (amount == null) return;

        boolean autoConfirm = cbAutoConfirm.isChecked();
        boolean autoPrint = cbAutoPrint.isChecked();
        boolean merchantReceipt = cbMerchantReceipt.isChecked();

        // Aqui você chamaria o equivalente em Java do 'startGenericPayment' do Kotlin
        // Exemplo hipotético (ajuste conforme a assinatura real do seu Paykit):
        /*
        paykit.startGenericPayment(amount, autoConfirm, autoPrint, merchantReceipt, result -> {
            runOnUiThread(() -> showPaymentResult(result));
        });
        */
        Toast.makeText(this, "Implementar chamada Genérica na SDK", Toast.LENGTH_SHORT).show();
    }

    private void executeQueryReport() {
        String jsonQueryStr = etJsonQuery.getText().toString().trim();
        JSONObject queryJson;
        try {
            queryJson = new JSONObject(jsonQueryStr);
        } catch (JSONException e) {
            Toast.makeText(this, "JSON de Consulta Inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aqui você chamaria o equivalente em Java do '(paykit as QueryReport).queryReport'
        /*
        if (paykit instanceof QueryReport) {
            ((QueryReport) paykit).queryReport(queryJson, result -> {
                 runOnUiThread(() -> showPaymentResult(result));
            });
        }
        */
        Toast.makeText(this, "Implementar chamada QueryReport na SDK", Toast.LENGTH_SHORT).show();
    }

    // Método auxiliar para não repetir o parse do valor
    private BigDecimal parseAmount(String amountStr) {
        if (amountStr.trim().isEmpty()) {
            Toast.makeText(this, "Informe o valor", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "O valor deve ser maior que zero", Toast.LENGTH_SHORT).show();
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void showPaymentResult(PaymentResult result) {
        layoutPaymentResult.setVisibility(View.VISIBLE);
        boolean success = result != null && (result.getStatus() == TransactionStatus.COMPLETED || result.getStatus() == TransactionStatus.APPROVED);

        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(success ? "SUCESSO" : "FALHA / NEGADO");
        if (result != null) {
            if (result.getId() != null) sb.append("\nID: ").append(result.getId());
            if (result.getTransactionInfo() != null && result.getTransactionInfo().getAuthorizationCode() != null) {
                sb.append("\nAuth Code: ").append(result.getTransactionInfo().getAuthorizationCode());
            }
            if (result.getMessage() != null) sb.append("\nMessage: ").append(result.getMessage());
            if (result.getRawData() != null) sb.append("\nRaw Data: ").append(result.getRawData());
        }

        tvPaymentResultStatus.setText(sb.toString());

        if (result != null && result.getMessage() != null && !result.getMessage().isEmpty()) {
            tvPaymentResultMessage.setVisibility(View.VISIBLE);
            tvPaymentResultMessage.setText("Detalhes: " + result.getMessage());
        } else {
            tvPaymentResultMessage.setVisibility(View.GONE);
        }
    }
}
