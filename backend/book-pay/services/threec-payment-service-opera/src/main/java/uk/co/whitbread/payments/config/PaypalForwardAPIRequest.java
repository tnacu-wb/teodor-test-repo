package uk.co.whitbread.payments.config;

import lombok.Data;

import java.util.List;

@Data
public class PaypalForwardAPIRequest {
    private String path;
    private List<List<String>> value;
}
