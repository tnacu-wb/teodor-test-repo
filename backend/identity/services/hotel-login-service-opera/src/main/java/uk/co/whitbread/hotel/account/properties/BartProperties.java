package uk.co.whitbread.hotel.account.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.properties.BaseBartProperties;

/**
 * Created by Oleksandr Murha on 31/10/2016.
 */
@Data
@Component
@RefreshScope
public class BartProperties implements BaseBartProperties {

    @Value("${bart.username}")
    private String username;
    @Value("${bart.password}")
    private String password;
    @Value("${bart.initSession.username}")
    private String initSessionUsername;
    @Value("${bart.initSession.password}")
    private String initSessionPassword;
    @Value("${bart.service.pi.guestService.url}")
    private String piGuestServiceUrl;
    @Value("${bart.service.pi.initSession.auth0Service.url}")
    private String piInitSessionAuth0ServiceUrl;
    @Value("${bart.service.bb.initSession.auth0Service.url}")
    private String bbInitSessionAuth0ServiceUrl;
    @Value("${bart.service.bb.clientService.url}")
    private String bbClientServiceUrl;
    @Value("${bart.service.booking.url}")
    private String bartBookingServiceUrl;

}
