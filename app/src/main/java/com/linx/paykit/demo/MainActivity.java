package com.linx.paykit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class MainActivity extends AppCompatActivity {

    private Paykit paykit;
    private ActivationParameters activationParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        paykit = buildDemoPaykit(this);

        // Parâmetros de ativação alinhados com a versão mais recente do demo (`ActivationScreen.kt`)
        activationParameters = new ActivationParameters(
                "12839955000116",
                new TefActivationParameters(),
                new PagSeguroActivationParameters(),
                new SitefActivationParameters(),
                TipoServidor.LinxTef,
                false,
                new SubAcquirerParameters()
        );

        // Configurações TEF atualizadas
        if (activationParameters.getTef() != null) {
            activationParameters.getTef().setToken("Rw$b05;$m2J6}Gq7wh@]");
            activationParameters.getTef().setHost("https://cposweb-hml.linxsaas.com.br/cposweb/api/conversor");
        }

        // Configurações SiTef atualizadas
        if (activationParameters.getSitef() != null) {
            activationParameters.getSitef().setSitefHost("https://cposweb-hml.linxsaas.com.br/cposweb/api/conversor");
            activationParameters.getSitef().setSitefCompanyCode("LINX0001");
            activationParameters.getSitef().setVanCnpj("54517628000198");
            activationParameters.getSitef().setAutomationCnpj("54517628000198");
            activationParameters.getSitef().setExternalCommunication("4"); // 0 em produção, 4 em homologação/teste
        }

        Button btnActivation = findViewById(R.id.btnActivation);
        if (btnActivation != null) {
            btnActivation.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, ActivationActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        Button btnPaymentTypes = findViewById(R.id.btnPaymentTypes);
        if (btnPaymentTypes != null) {
            btnPaymentTypes.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, PaymentActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }

    private Paykit buildDemoPaykit(android.app.Activity activity) {
        return PaykitBuilder.buildPaykit((androidx.activity.ComponentActivity) activity);
    }

    private void showActivationResult(ActivationResult result) {
        LinearLayout layoutResult = findViewById(R.id.layoutResult);
        TextView tvResultStatus = findViewById(R.id.tvResultStatus);
        TextView tvResultMessage = findViewById(R.id.tvResultMessage);

        if (layoutResult != null && tvResultStatus != null && tvResultMessage != null) {
            layoutResult.setVisibility(View.VISIBLE);
            boolean success = result != null && result.getSuccess();
            
            String statusText = "Status: " + success;
            if (result != null && result.getMessage() != null) {
                statusText += "\nMessage: " + result.getMessage();
            }
            if (result != null && result.getRawData() != null) {
                statusText += "\nData: " + result.getRawData();
            }
            
            tvResultStatus.setText(statusText);
            
            String message = result != null ? result.getMessage() : null;
            if (!success && message != null && !message.isEmpty()) {
                tvResultMessage.setVisibility(View.VISIBLE);
                tvResultMessage.setText("Status message: " + message);
            } else {
                tvResultMessage.setVisibility(View.GONE);
            }
        }
    }
}
