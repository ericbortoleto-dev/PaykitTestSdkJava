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
                        new PaykitId("HML-B256A8D9-42B9-45DA-AB63-D31FA5BEF748"), // 1 por automação comercial por ambiente (DEV/HML/PRD) - fornecido pela Linx
                        null
                )
        );
    }
}
