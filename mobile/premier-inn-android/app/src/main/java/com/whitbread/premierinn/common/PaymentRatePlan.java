package com.whitbread.premierinn.common;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

/**
 * Class for the state of the prepayment flags options
 * <p>
 * prepaymentRequired   prepaymentAllowed   guaranteeRequired   Payment Option
 * -----------------------------------------------------------------------------
 * False                False               False               Pay on Arrival
 * False                False               True                Pay on Arrival
 * False                True                False               Pay on Arrival and Pay Now
 * False                True                True                Pay on Arrival and Pay Now
 * True                 True                True                Pay Now
 * True                 True                False               Pay Now
 *
 * @see <a href="https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/113970401/Payment+Processes">Payment Processes</a>
 */

@AutoValue
public abstract class PaymentRatePlan implements Parcelable {

    public abstract boolean prepaymentAllowed();

    public abstract boolean prepaymentRequired();

    public abstract boolean guaranteeRequired();

    @NonNull
    public static PaymentRatePlan create(boolean prepaymentAllowed, boolean prepaymentRequired, boolean guaranteeRequired) {
        return new AutoValue_PaymentRatePlan(prepaymentAllowed, prepaymentRequired, guaranteeRequired);
    }

    public PaymentTimingRule paymentOption() {
        if (prepaymentRequired()) {
            return PaymentTimingRule.PAY_NOW;
        }

        if (!prepaymentAllowed()) {
            return PaymentTimingRule.PAY_LATER;
        }

        return PaymentTimingRule.PAY_NOW_OR_PAY_LATER;
    }

}
