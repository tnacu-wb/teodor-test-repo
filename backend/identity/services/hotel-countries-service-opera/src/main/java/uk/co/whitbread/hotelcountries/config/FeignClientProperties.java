package uk.co.whitbread.hotelcountries.config;

import lombok.Data;

@Data
public class FeignClientProperties {
    private String url;
    private String country;
    private String language;
    private String resource;
    private String username;
    private String password;
}
