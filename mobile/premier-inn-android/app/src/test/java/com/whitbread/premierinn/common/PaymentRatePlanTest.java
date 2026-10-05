package com.whitbread.premierinn.common;

import org.junit.Test;

import static com.whitbread.premierinn.common.PaymentTimingRule.PAY_NOW;
import static com.whitbread.premierinn.common.PaymentTimingRule.PAY_NOW_OR_PAY_LATER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
public class PaymentRatePlanTest {

    @Test
    public void paymentOption_case1() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(false, false, false);

        assertThat(plan.paymentOption(), is(PaymentTimingRule.PAY_LATER));
    }

    @Test
    public void paymentOption_case2() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(false, false, true);

        assertThat(plan.paymentOption(), is(PaymentTimingRule.PAY_LATER));
    }

    @Test
    public void paymentOption_case3() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(true, false, true);

        assertThat(plan.paymentOption(), is(PAY_NOW_OR_PAY_LATER));
    }

    @Test
    public void paymentOption_case4() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(true, false, false);

        assertThat(plan.paymentOption(), is(PAY_NOW_OR_PAY_LATER));
    }

    @Test
    public void paymentOption_case5() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(true, true, true);

        assertThat(plan.paymentOption(), is(PAY_NOW));
    }

    @Test
    public void paymentOption_case6() throws Exception {
        PaymentRatePlan plan = PaymentRatePlan.create(true, true, false);

        assertThat(plan.paymentOption(), is(PAY_NOW));
    }
}