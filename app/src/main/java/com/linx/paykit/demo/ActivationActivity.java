package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linx.paykit.common.activation.ActivationParameters;
import com.linx.paykit.common.activation.ActivationResult;
import com.linx.paykit.common.activation.PagSeguroActivationParameters;
import com.linx.paykit.common.activation.SitefActivationParameters;
import com.linx.paykit.common.activation.SubAcquirerParameters;
import com.linx.paykit.common.activation.TefActivationParameters;
import com.linx.paykit.common.activation.TipoServidor;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.ActivationCache;
import com.linx.paykit.demo.util.PaykitBuilder;

public class ActivationActivity extends AppCompatActivity {

    private Paykit paykit;

    private EditText etCnpj;
    private EditText etToken;
    private EditText etHost;
    private Button btnDoActivation;

    private LinearLayout layoutActivationResult;
    private TextView tvActivationResultStatus;
    private TextView tvActivationResultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        paykit = PaykitBuilder.buildPaykit(this);

        etCnpj = findViewById(R.id.etCnpj);
        etToken = findViewById(R.id.etToken);
        etHost = findViewById(R.id.etHost);
        btnDoActivation = findViewById(R.id.btnDoActivation);

        layoutActivationResult = findViewById(R.id.layoutActivationResult);
        tvActivationResultStatus = findViewById(R.id.tvActivationResultStatus);
        tvActivationResultMessage = findViewById(R.id.tvActivationResultMessage);

        btnDoActivation.setOnClickListener(v -> executeActivation());
    }

    private void executeActivation() {
        String cnpj = etCnpj.getText().toString().trim();
        String token = etToken.getText().toString().trim();
        String host = etHost.getText().toString().trim();

        if (cnpj.isEmpty()) {
            Toast.makeText(this, "Informe o CNPJ", Toast.LENGTH_SHORT).show();
            return;
        }

        ActivationParameters activationParameters = new ActivationParameters(
                cnpj,
                new TefActivationParameters(),
                new PagSeguroActivationParameters(),
                new SitefActivationParameters(),
                TipoServidor.LinxTef,
                false,
                new SubAcquirerParameters()
        );

        if (activationParameters.getTef() != null) {
            activationParameters.getTef().setToken(token);
            activationParameters.getTef().setHost(host);
        }

        if (activationParameters.getSitef() != null) {
            activationParameters.getSitef().setSitefHost(host);
            activationParameters.getSitef().setSitefCompanyCode("LINX0001");
            activationParameters.getSitef().setVanCnpj("54517628000198");
            activationParameters.getSitef().setAutomationCnpj("54517628000198");
            activationParameters.getSitef().setExternalCommunication("4");
        }

        // Verifica cache de ativação
        boolean isActivated = ActivationCache.loadActivationState(this, activationParameters);
        if (isActivated) {
            Toast.makeText(this, "Terminal já está ativado com estes parâmetros!", Toast.LENGTH_SHORT).show();
            showActivationResult(new ActivationResult(null, null, true, "Terminal já está ativado", null));
            return;
        }

        paykit.activate(activationParameters, result -> runOnUiThread(() -> {
            showActivationResult(result);
            boolean success = result != null && result.getSuccess();
            String message = result != null ? result.getMessage() : "Sem resposta";
            if (success) {
                ActivationCache.saveActivationState(ActivationActivity.this, activationParameters);
                Toast.makeText(ActivationActivity.this, "Terminal ativado com sucesso!", Toast.LENGTH_LONG).show();
            } else {
                ActivationCache.clearActivationState(ActivationActivity.this);
                Toast.makeText(ActivationActivity.this, "Falha na ativação: " + message, Toast.LENGTH_LONG).show();
            }
        }));
    }

    private void showActivationResult(ActivationResult result) {
        layoutActivationResult.setVisibility(View.VISIBLE);
        boolean success = result != null && result.getSuccess();

        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(success ? "SUCESSO" : "FALHA");
        if (result != null) {
            if (result.getMessage() != null) sb.append("\nMessage: ").append(result.getMessage());
            if (result.getRawData() != null) sb.append("\nData: ").append(result.getRawData());
        }

        tvActivationResultStatus.setText(sb.toString());

        String message = result != null ? result.getMessage() : null;
        if (!success && message != null && !message.isEmpty()) {
            tvActivationResultMessage.setVisibility(View.VISIBLE);
            tvActivationResultMessage.setText("Detalhes: " + message);
        } else {
            tvActivationResultMessage.setVisibility(View.GONE);
        }
    }
}
