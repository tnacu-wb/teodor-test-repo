package uk.co.whitbread.contentservice.roomtypes.config.properties;

import lombok.Data;

@Data
public class CacheConfigProperty {
    private String name;
    private long ttl;
}