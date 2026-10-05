package uk.co.whitbread.hotel.payment.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class XmlValidatorTest {

    private XmlValidator xmlValidator;

    @BeforeEach
    public void setUp() throws Exception {
        this.xmlValidator = new XmlValidator();
    }

    @Test
    public void validate_shouldHandleValidXml() throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get("src/test/resources/mapping/sample-pares.xml")));
        assertTrue(xmlValidator.validate(xml));
    }

    @Test
    public void validate_shouldHandleInvalidXml() throws Exception {
        String xml = "<first>foo</name></first>";
        assertFalse(xmlValidator.validate(xml));
    }

}