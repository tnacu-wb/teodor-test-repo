package com.whitbread.premierinn.common.analytics;

import org.junit.Test;

import java.util.Arrays;

import static com.whitbread.premierinn.common.analytics.ProductValues.EMPTY;
import static com.whitbread.premierinn.common.analytics.ProductValues.UNAVAILABLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/*
A;B;C;D; E1|E2|E3 ; F1|F2|F3|F4|F6
A;B;C;D; E1|E2|E3 ; F1|F2|F3|F4|F5|F6
;B;;;

HDS **** |
&&products:";LONBLA;;;|event61=176.00|event60=171.00;eVar50=0.2|eVar53=176.00|eVar52=171.00|eVar54=Rate Not Available|eVar55=1",

SRS **** |
&&products:";LONBLA;;;|event61=176.00|event60=171.00;eVar50=0.2|eVar53=176.00|eVar52=171.00|eVar54=Rate Not Available|eVar55=1,

Confirmation
;LONSOU;4;1187.00;event37=126.00|event20=1058.00|event31=2; **** ;

&&events: "event37,event82=3,event37=126.00,event20=1058.00,event31=2, event54=0.30", ****event37
 */
public class ProductValuesTest {

    //[Category1];[Product1];[Quantity];[Total Price];

    @Test
    public void just_product_with_empty_category() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("")
                .product("LONCIT")
                .quantity(UNAVAILABLE)
                .totalPrice(UNAVAILABLE)
                .build();

        assertThat(values.toString(), is(";LONCIT"));
    }

    @Test
    public void category_and_product() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("cat")
                .product("LONCIT")
                .quantity(UNAVAILABLE)
                .totalPrice(UNAVAILABLE)
                .build();

        assertThat(values.toString(), is("cat;LONCIT"));
    }

    @Test
    public void category_and_product_empty_quantity() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("cat")
                .product("LONCIT")
                .quantity(EMPTY)
                .totalPrice(UNAVAILABLE)
                .build();

        assertThat(values.toString(), is("cat;LONCIT;"));
    }

    @Test
    public void category_and_product_empty_quantity_empty_totalPrice() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("cat")
                .product("LONCIT")
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .build();

        assertThat(values.toString(), is("cat;LONCIT;;"));
    }

    @Test
    public void all_empty_just_product() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("")
                .product("LONCIT")
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .build();

        assertThat(values.toString(), is(";LONCIT;;"));
    }

    @Test
    public void just_product_and_event() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("")
                .product("LONCIT")
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .evars(Arrays.asList("evar50=2.0"))
                .build();

        assertThat(values.toString(), is(";LONCIT;;;evar50=2.0"));
    }

    @Test
    public void just_product_and_events() throws Exception {

        ProductValues values = ProductValues.builder()
                .category("")
                .product("LONCIT")
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .evars(Arrays.asList("evar50=2.0", "evar51=3.0"))
                .build();

        assertThat(values.toString(), is(";LONCIT;;;evar50=2.0|evar51=3.0"));
    }
}