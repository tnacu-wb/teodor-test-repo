package uk.co.whitbread.hotel.payment.service.utils;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Component
public class ParesUtils {
    public String decodePares(String pares) throws IOException, DataFormatException {

        pares = pares.replaceAll("[^-_A-Za-z0-9+/=]", "");
        byte[] strData = Base64.getDecoder().decode(pares);

        return new String(decompress(strData));
    }

    private byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }
}
