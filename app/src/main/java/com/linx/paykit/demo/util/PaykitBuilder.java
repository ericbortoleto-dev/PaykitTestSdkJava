package com.linx.paykit.demo.util;

import android.content.Context;
import androidx.activity.ComponentActivity;

import com.linx.paykit.common.builder.Parameters;
import com.linx.paykit.common.builder.PaykitId;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.core.PaykitFactory;
import com.linx.paykit.ui.data.PaykitUiModel;

public class PaykitBuilder {

    private static Paykit instance;

    public static synchronized Paykit buildPaykit(ComponentActivity activity) {
        if (instance == null) {
            if (activity != null) {
                PaykitUiModel.Companion.build(
                        activity,
                        null,                               // callback opcional
                        true,                                       // useInternalScreens
                        true,                                       // isDebuggable
                        kotlinx.coroutines.Dispatchers.getIO()      // dispatcher IO
                );
            }

            Context appContext = activity != null ? activity.getApplicationContext() : null;
            instance = new PaykitFactory().build(
                    new Parameters(
                            appContext != null ? appContext : (activity != null ? activity : null),
                            "AppTeste",
                            new PaykitId("HML-B256A8D9-42B9-45DA-AB63-D31FA5BEF748"), // 1 por automação comercial por ambiente
                            null
                    )
            );
        }
        return instance;
    }

    public static synchronized Paykit buildPaykit(Context context) {
        if (instance == null) {
            ComponentActivity activity = null;
            if (context instanceof ComponentActivity) {
                activity = (ComponentActivity) context;
            }
            if (activity != null) {
                PaykitUiModel.Companion.build(
                        activity,
                        null,
                        true,
                        true,
                        kotlinx.coroutines.Dispatchers.getIO()
                );
            }

            instance = new PaykitFactory().build(
                    new Parameters(
                            context.getApplicationContext(),
                            "AppTeste",
                            new PaykitId("HML-B256A8D9-42B9-45DA-AB63-D31FA5BEF748"),
                            null
                    )
            );
        }
        return instance;
    }
}
