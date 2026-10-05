package uk.co.whitbread.hotelcountries.config.properties;

import lombok.Data;

@Data
public class CacheConfigProperty {
    private String name;
    private long ttl;
    private long maxSize;
}