package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linx.paykit.common.features.PendingTransactionHandler;
import com.linx.paykit.common.parameter.PendingTransactionParameters;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.PaykitBuilder;

public class TransactionManagementActivity extends AppCompatActivity {

    private Paykit paykit;

    private EditText etTransactionId;
    private Button btnConfirm;
    private Button btnUndo;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private TextView tvResultStatus;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_management);

        paykit = PaykitBuilder.buildPaykit(this);

        etTransactionId = findViewById(R.id.etTransactionId);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnUndo = findViewById(R.id.btnUndo);
        progressBar = findViewById(R.id.progressBar);
        layoutResult = findViewById(R.id.layoutResult);
        tvResultStatus = findViewById(R.id.tvResultStatus);
        btnClose = findViewById(R.id.btnClose);

        btnConfirm.setOnClickListener(v -> executeConfirm());
        btnUndo.setOnClickListener(v -> executeUndo());
        btnClose.setOnClickListener(v -> finish());

        // Preenche com o ID da última transação se houver
        String lastTxId = com.linx.paykit.demo.util.LastTransactionHolder.getLastTransactionId();
        if (lastTxId != null && !lastTxId.isEmpty()) {
            etTransactionId.setText(lastTxId);
        }
    }

    private void executeConfirm() {
        String transactionId = etTransactionId.getText().toString().trim();
        if (transactionId.isEmpty()) {
            Toast.makeText(this, "Informe o ID da transação", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        btnConfirm.setEnabled(false);
        btnUndo.setEnabled(false);

        PendingTransactionParameters params = new PendingTransactionParameters(transactionId,null,true);

        if (paykit instanceof PendingTransactionHandler) {
            ((PendingTransactionHandler) paykit).confirmPendingTransaction(params, result -> runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                btnUndo.setEnabled(true);
                layoutResult.setVisibility(View.VISIBLE);

                String msg = result ? "Transação confirmada" : "Falha ao confirmar";
                tvResultStatus.setText(msg);
                Toast.makeText(TransactionManagementActivity.this, msg, Toast.LENGTH_SHORT).show();
            }));
        } else {
            progressBar.setVisibility(View.GONE);
            btnConfirm.setEnabled(true);
            btnUndo.setEnabled(true);
            Toast.makeText(this, "Paykit não suporta PendingTransactionHandler", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeUndo() {
        String transactionId = etTransactionId.getText().toString().trim();
        if (transactionId.isEmpty()) {
            Toast.makeText(this, "Informe o ID da transação", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        btnConfirm.setEnabled(false);
        btnUndo.setEnabled(false);

        PendingTransactionParameters params = new PendingTransactionParameters(transactionId,null ,true);

        if (paykit instanceof PendingTransactionHandler) {
            ((PendingTransactionHandler) paykit).undoPendingTransaction(params, result -> runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                btnUndo.setEnabled(true);
                layoutResult.setVisibility(View.VISIBLE);

                String msg = result ? "Transação desfeita" : "Falha ao desfazer";
                tvResultStatus.setText(msg);
                Toast.makeText(TransactionManagementActivity.this, msg, Toast.LENGTH_SHORT).show();
            }));
        } else {
            progressBar.setVisibility(View.GONE);
            btnConfirm.setEnabled(true);
            btnUndo.setEnabled(true);
            Toast.makeText(this, "Paykit não suporta PendingTransactionHandler", Toast.LENGTH_SHORT).show();
        }
    }
}
