package uk.co.whitbread.hotel.payment.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

@Component
@Slf4j
public class XmlValidator {
    private DocumentBuilder documentBuilder;

    public XmlValidator() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        documentBuilder = factory.newDocumentBuilder();
    }

    public boolean validate(String xml) {
        try {
            documentBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            return true;
        } catch (Exception e) {
            log.warn("Exception when validating XML, {}", e.getMessage());
            return false;
        }
    }
}
