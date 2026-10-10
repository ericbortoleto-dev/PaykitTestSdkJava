package com.linx.paykit.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.linx.paykit.common.activation.ActivationParameters;
import com.linx.paykit.common.activation.GSurfActivationParameters;
import com.linx.paykit.common.activation.PagSeguroActivationParameters;
import com.linx.paykit.common.activation.SitefActivationParameters;
import com.linx.paykit.common.activation.SubAcquirerParameters;
import com.linx.paykit.common.activation.TefActivationParameters;
import com.linx.paykit.common.activation.TipoServidor;
import com.linx.paykit.common.parameter.ReceiptType;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.PaykitBuilder;

public class MainActivity extends AppCompatActivity {

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

        Paykit paykit = PaykitBuilder.buildPaykit(this);

        // Parâmetros de ativação alinhados com a versão mais recente do demo (`ActivationScreen.kt`)
        activationParameters = new ActivationParameters(
                "12839955000116",
                new TefActivationParameters(),
                new PagSeguroActivationParameters(),
                new SitefActivationParameters(),
                TipoServidor.LinxTef,
                false,
                new SubAcquirerParameters(),
                new GSurfActivationParameters()
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

        Button btnGetNsuOrToken = findViewById(R.id.btnGetNsuOrToken);
        if (btnGetNsuOrToken != null) {
            btnGetNsuOrToken.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, GetTransactionActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        Button btnPreAuthorization = findViewById(R.id.btnPreAuthorization);
        if (btnPreAuthorization != null) {
            btnPreAuthorization.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, PreAuthorizationActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        Button btnManagePreAuthorization = findViewById(R.id.btnManagePreAuthorization);
        if (btnManagePreAuthorization != null) {
            btnManagePreAuthorization.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ManageTransaction.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        Button btnPrintCustomerReceipt = findViewById(R.id.btnPrintCustomerReceipt);
        if (btnPrintCustomerReceipt != null) {
            btnPrintCustomerReceipt.setOnClickListener(view -> {
                paykit.printLastReceipt(ReceiptType.CLIENT, result -> {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(MainActivity.this, result.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    })
                ;});
            });
        }

        Button btnPrintStoreReceipt = findViewById(R.id.btnPrintStoreReceipt);
        if (btnPrintStoreReceipt != null) {
            btnPrintStoreReceipt.setOnClickListener(view -> {
                paykit.printLastReceipt(ReceiptType.CLIENT, result -> {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(MainActivity.this, result.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    })
                ;});
            });
        }
    }
}
