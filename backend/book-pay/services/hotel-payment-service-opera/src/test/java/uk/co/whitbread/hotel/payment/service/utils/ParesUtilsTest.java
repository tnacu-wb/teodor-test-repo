package uk.co.whitbread.hotel.payment.service.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParesUtilsTest {

    private ParesUtils paresUtils;

    @BeforeEach
    public void setup() {
        this.paresUtils = new ParesUtils();
    }

    @Test
    public void decodePares_shouldDecodeValidPares() throws Exception {
        String pares = "eJ\nyzScssKi6xs8lLzE21S8vPt9EHs2z0IeIAs1ULHQ==";
        String expected = "<first><name>foo</name></first>";

        String decodedPares = paresUtils.decodePares(pares);

        assertEquals(expected, decodedPares);
    }

    @Test
    public void decodePares_shouldThrowExecptionForInvalidPares() throws Exception {
        String pares = "eJyzScssKi6xs8lLzE21Sasdasdrgtrbe9EHs2z0IeIAs1ULHQ==";
        assertThrows(DataFormatException.class, () ->
            paresUtils.decodePares(pares));
    }
}