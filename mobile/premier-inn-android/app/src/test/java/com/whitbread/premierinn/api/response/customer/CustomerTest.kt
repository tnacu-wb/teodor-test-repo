package com.whitbread.premierinn.api.response.customer

import com.google.gson.Gson
import com.whitbread.premierinn.domain.customer.entity.Customer
import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CustomerTest {

    @Test
    fun `customer_typeAdapter_success`() {
        val customerJson = File("./src/test/resources/apiTest/ui-customer-success.json").inputStream().readBytes()
                .toString(Charsets.UTF_8)
        val customerData = Gson().fromJson<Customer>(customerJson, Customer::class.java)
        assertNotNull(customerData)
    }

    @Test
    fun `customer_has_paymentCard`() {
        val customerJson = File("./src/test/resources/apiTest/ui-customer-success.json").inputStream().readBytes()
                .toString(Charsets.UTF_8)
        val customerData = Gson().fromJson<Customer>(customerJson, Customer::class.java)

        assertTrue(customerData.hasPaymentCardDetails())
    }

    @Test
    fun `customer_has_no_paymentCard`() {
        val customerJson = File("./src/test/resources/apiTest/ui-customer-success-no-payment.json").inputStream().readBytes()
                .toString(Charsets.UTF_8)
        val customerData = Gson().fromJson<Customer>(customerJson, Customer::class.java)

        assertFalse(customerData.hasPaymentCardDetails())
    }
}