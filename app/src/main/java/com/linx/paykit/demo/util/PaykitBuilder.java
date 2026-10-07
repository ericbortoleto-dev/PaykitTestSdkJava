package com.linx.paykit.demo.util;

import androidx.activity.ComponentActivity;

import com.linx.paykit.common.builder.Parameters;
import com.linx.paykit.common.builder.PaykitId;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.core.PaykitFactory;
import com.linx.paykit.ui.data.PaykitUiModel;

public class PaykitBuilder {

    public static Paykit buildPaykit(ComponentActivity activity) {
        // Métodos de companion object em Kotlin sem @JvmStatic são acessados via .Companion em Java
        PaykitUiModel.Companion.build(
                activity,
                null,
                true,
                false,
                kotlinx.coroutines.Dispatchers.getIO()
        );

        return new PaykitFactory().build(
                new Parameters(
                        activity,
                        "AppTeste",
                        new PaykitId("DEV-F2A7D071-3C89-4AB3-8FB8-21F29B62B813"), // 1 por automação comercial por ambiente (DEV/HML/PRD) - fornecido pela Linx
                        null
                )
        );
    }
}
