package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linx.paykit.common.TransactionQueryResult;
import com.linx.paykit.common.features.TransactionQuery;
import com.linx.paykit.common.parameter.TransactionQueryParameters;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.PaykitBuilder;

import java.text.NumberFormat;
import java.util.Locale;

public class GetTransactionActivity extends AppCompatActivity {

    private Paykit paykit;

    private EditText etTransactionId;
    private CheckBox cbIsExternalId;
    private Button btnSearch;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private TextView tvTransactionInfoTitle;
    private TextView tvTransactionInfoSubtitle;
    private TextView tvTransactionInfoDetails;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_transaction);

        paykit = PaykitBuilder.buildPaykit(this);

        etTransactionId = findViewById(R.id.etTransactionId);
        cbIsExternalId = findViewById(R.id.cbIsExternalId);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        layoutResult = findViewById(R.id.layoutResult);
        tvTransactionInfoTitle = findViewById(R.id.tvTransactionInfoTitle);
        tvTransactionInfoSubtitle = findViewById(R.id.tvTransactionInfoSubtitle);
        tvTransactionInfoDetails = findViewById(R.id.tvTransactionInfoDetails);
        btnClose = findViewById(R.id.btnClose);

        btnSearch.setOnClickListener(v -> executeQuery());
        btnClose.setOnClickListener(v -> finish());

        // Preenche automaticamente com a última transação aprovada (se houver) para facilitar testes
        String lastTxId = com.linx.paykit.demo.util.LastTransactionHolder.getLastTransactionId();
        String lastExtId = com.linx.paykit.demo.util.LastTransactionHolder.getLastExternalId();

        if (lastExtId != null && !lastExtId.isEmpty()) {
            etTransactionId.setText(lastExtId);
            cbIsExternalId.setChecked(true);
        } else if (lastTxId != null && !lastTxId.isEmpty()) {
            etTransactionId.setText(lastTxId);
            cbIsExternalId.setChecked(false);
        }
    }

    private void executeQuery() {
        String transactionId = etTransactionId.getText().toString().trim();
        if (transactionId.isEmpty()) {
            Toast.makeText(this, "Informe o ID da transação", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isExtId = cbIsExternalId.isChecked();

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        btnSearch.setEnabled(false);

        TransactionQueryParameters queryParameters = new TransactionQueryParameters();
        if (isExtId) {
            queryParameters.setExternalId(transactionId);
        } else {
            queryParameters.setTransactionId(transactionId);
        }

        if (paykit instanceof TransactionQuery) {
            ((TransactionQuery) paykit).getTransaction(queryParameters, result -> runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnSearch.setEnabled(true);
                layoutResult.setVisibility(View.VISIBLE);

                if (result == null || result.getId() == null) {
                    tvTransactionInfoTitle.setText("Transação não encontrada");
                    tvTransactionInfoSubtitle.setText("");
                    tvTransactionInfoDetails.setText("");
                    Toast.makeText(GetTransactionActivity.this, "Transação não encontrada", Toast.LENGTH_SHORT).show();
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    String amountFormatted = result.getAmount() != null ? currencyFormat.format(result.getAmount()) : "R$ 0,00";

                    tvTransactionInfoTitle.setText("ID da Transação - " + result.getId());
                    tvTransactionInfoSubtitle.setText(amountFormatted + " - " + result.getStatus());
                    tvTransactionInfoDetails.setText(result.getProcessor() + " - " + result.getDateTime() + "\nRaw: " + result.getRawData());

                    Toast.makeText(GetTransactionActivity.this, "Transação encontrada com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }));
        } else {
            progressBar.setVisibility(View.GONE);
            btnSearch.setEnabled(true);
            Toast.makeText(this, "Paykit não suporta TransactionQuery", Toast.LENGTH_SHORT).show();
        }
    }
}
