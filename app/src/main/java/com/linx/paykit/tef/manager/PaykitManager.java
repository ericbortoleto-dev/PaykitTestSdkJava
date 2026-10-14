package com.linx.paykit.tef.manager;

import androidx.activity.ComponentActivity;

import com.linx.paykit.common.Callback;
import com.linx.paykit.common.PaymentResult;
import com.linx.paykit.common.activation.ActivationParameters;
import com.linx.paykit.common.activation.ActivationResult;
import com.linx.paykit.common.parameter.CreditParameters;
import com.linx.paykit.common.parameter.DebitParameters;
import com.linx.paykit.common.parameter.PaymentParameters;
import com.linx.paykit.common.parameter.StartPaymentParameters;
import com.linx.paykit.common.parameter.VoucherParameters;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.demo.util.PaykitBuilder;

public class PaykitManager {

    private final Paykit paykit;

    public PaykitManager(ComponentActivity activity) {
        this.paykit = PaykitBuilder.buildPaykit(activity);
    }

    public Paykit getPaykit() {
        return paykit;
    }

    public void activate(ActivationParameters parameters, Callback<ActivationResult> callback) {
        if (paykit != null) {
            paykit.activate(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }

    public void credit(CreditParameters parameters, Callback<PaymentResult> callback) {
        if (paykit != null) {
            paykit.credit(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }

    public void debit(DebitParameters parameters, Callback<PaymentResult> callback) {
        if (paykit != null) {
            paykit.debit(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }

    public void pix(PaymentParameters parameters, Callback<PaymentResult> callback) {
        if (paykit != null) {
            paykit.pix(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }

    public void voucher(VoucherParameters parameters, Callback<PaymentResult> callback) {
        if (paykit != null) {
            paykit.voucher(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }

    public void startPayment(StartPaymentParameters parameters, Callback<PaymentResult> callback) {
        if (paykit != null) {
            paykit.startPayment(parameters, callback);
        } else if (callback != null) {
            callback.execute(null);
        }
    }
}
