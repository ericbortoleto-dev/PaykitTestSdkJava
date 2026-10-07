package com.linx.paykit.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.linx.paykit.common.activation.ActivationParameters;
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

        paykit = PaykitBuilder.buildPaykit(this);

        activationParameters = new ActivationParameters(
                "12839955000116",
                new TefActivationParameters(),
                new PagSeguroActivationParameters(),
                new SitefActivationParameters(),
                TipoServidor.LinxTef,
                false,
                new SubAcquirerParameters()
        ); // 1 por cliente

        // Configurações Linxtef
        if (activationParameters.getTef() != null) {
            activationParameters.getTef().setProduction(false);
            activationParameters.getTef().setToken("Rw$b05;$m2J6}Gq7wh@]"); // 1 por ambiente (HML/PRD) - fornecido pela Linx
        }

        // Configurações PagSeguro
        if (activationParameters.getPagSeguro() != null) {
            activationParameters.getPagSeguro().setActivationCode("ACTIVATION_CODE_PAGSEGURO"); // 1 por cliente - fornecido pelo PagSeguro
        }

        // Configurações SiTef
        if (activationParameters.getSitef() != null) {
            activationParameters.getSitef().setSitefHost("HOST_SITEF");
            activationParameters.getSitef().setSitefCompanyCode("CODIGO_EMPRESA_SITEF"); // 1 por cliente - fornecido pelo SiTef
            activationParameters.getSitef().setTlsRegistrationToken("TOKEN_SITEF"); // 1 por X ativações e/ou Y tempo - fornecido pelo SiTef
        }

        Button btnActivation = findViewById(R.id.btnActivation);
        if (btnActivation != null) {
            btnActivation.setOnClickListener(v -> {
                if (paykit != null && activationParameters != null) {
                    // Verifica se o terminal já está ativado com estes mesmos parâmetros (cache)
                    boolean isActivated = ActivationCache.loadActivationState(MainActivity.this, activationParameters);
                    if (isActivated) {
                        Toast.makeText(MainActivity.this, "Terminal já está ativado!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    paykit.activate(activationParameters, result -> {
                        runOnUiThread(() -> {
                            boolean success = result != null && result.getSuccess();
                            String message = result != null ? result.getMessage() : "Sem resposta";
                            if (success) {
                                ActivationCache.saveActivationState(MainActivity.this, activationParameters);
                                Toast.makeText(MainActivity.this, "Terminal ativado com sucesso!", Toast.LENGTH_LONG).show();
                            } else {
                                ActivationCache.clearActivationState(MainActivity.this);
                                Toast.makeText(MainActivity.this, "Falha na ativação: " + message, Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Paykit ou parâmetros não inicializados", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
