package uk.co.whitbread.piba.api.converter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

@Data
@Component
public class WorldlineRequestTransformer {

    private WorldLineProperties worldLineProperties;
    private TrustedPartnerCredentialsType credentials;
    private TrustedPartnerCredentialsType deCredentials;
    private TrustedPartnerCredentialsType gbCredentials;
    private HeaderType header;
    private HeaderType deHeader;
    private HeaderType gbHeader;

    @Autowired
    public WorldlineRequestTransformer(WorldLineProperties worldLineProperties) {
        credentials = new TrustedPartnerCredentialsType();
        credentials.setUsername(worldLineProperties.getPiba().getUsername());
        credentials.setPassword("");
        header = new HeaderType();
        header.setClientMessageId(worldLineProperties.getClientMessageId());
        header.setCultureCode(worldLineProperties.getCultureCode());

        deCredentials = new TrustedPartnerCredentialsType();
        deCredentials.setUsername(worldLineProperties.getDe().getUsername());
        deCredentials.setPassword("");
        deHeader = new HeaderType();
        deHeader.setClientMessageId(worldLineProperties.getDe().getClientMessageId());
        deHeader.setCultureCode(worldLineProperties.getDe().getCultureCode());

        gbCredentials = new TrustedPartnerCredentialsType();
        gbCredentials.setUsername(worldLineProperties.getGb().getUsername());
        gbCredentials.setPassword("");
        gbHeader = new HeaderType();
        gbHeader.setClientMessageId(worldLineProperties.getGb().getClientMessageId());
        gbHeader.setCultureCode(worldLineProperties.getGb().getCultureCode());

    }

}
