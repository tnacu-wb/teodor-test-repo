package uk.co.whitbread.company.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class PaymentCardMaskerTest {
    private PaymentCardMasker sut;

    @BeforeEach
    void setUp() {
        sut = new PaymentCardMasker();
    }

    @Test
    void maskNumber() {
        //when//then
        assertThat(sut.maskNumber("4444333322221111"), is("************1111"));
    }
}
