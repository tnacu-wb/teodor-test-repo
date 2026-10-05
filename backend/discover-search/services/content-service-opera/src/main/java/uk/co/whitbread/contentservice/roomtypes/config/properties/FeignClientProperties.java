package uk.co.whitbread.contentservice.roomtypes.config.properties;

import lombok.Data;

@Data
public class FeignClientProperties {
    private String url;
    private String resource;
    private String ratesresource;
    private String cookiePoliciesResource;
    private String bookingNotificationsResource;
    private String username;
    private String password;
}
