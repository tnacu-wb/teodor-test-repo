package uk.co.whitbread.payments.service;

import java.time.LocalDate;
import java.util.Map;

public interface TemplateService {
    String getRedirectHtml(String environment, Map<String, String> queryParams, String paymentId, String paymentStatus);
    String getIPageHtml(String session, String language, String paymentId, LocalDate departureDate);
}

