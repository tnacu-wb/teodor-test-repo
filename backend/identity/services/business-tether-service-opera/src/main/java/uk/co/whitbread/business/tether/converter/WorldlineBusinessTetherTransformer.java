package uk.co.whitbread.business.tether.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidRequest;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.piba.api.converter.WorldlineRequestTransformer;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserRequestType;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumberRequestType;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumberRequestType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "worldline.tetheringPlus.enabled", havingValue = "true")
public class WorldlineBusinessTetherTransformer extends WorldlineRequestTransformer implements WorldlineTransformer {

    @Autowired
    public WorldlineBusinessTetherTransformer(WorldLineProperties worldLineProperties) {
        super(worldLineProperties);
    }

    @Override
    public TetherByAccountNumber toTetherByAccountRequest(TetherLinkRequest tetherLinkRequest, Scheme scheme) {

        TetherByAccountNumberRequestType request = new TetherByAccountNumberRequestType();
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        request.setLinkCode(tetherLinkRequest.getLinkCode());
        request.setAccountNumber(tetherLinkRequest.getLinkId());
        request.setNewMemorableWord(tetherLinkRequest.getMemorableWord());
    
        TetherByAccountNumber tetherByAccount = new TetherByAccountNumber();
        tetherByAccount.setRequest(request);

        return tetherByAccount;
    }

    @Override
    public TetherByCardNumber toTetherByCardRequest(TetherLinkRequest tetherLinkRequest, Scheme scheme) {
        TetherByCardNumberRequestType request = new TetherByCardNumberRequestType();

        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        request.setLinkCode(tetherLinkRequest.getLinkCode());
        request.setCardNumber(tetherLinkRequest.getLinkId());
        request.setNewMemorableWord(tetherLinkRequest.getMemorableWord());
    
        TetherByCardNumber tetherByCard = new TetherByCardNumber();
        tetherByCard.setRequest(request);
        return tetherByCard;
    }
    
    @Override
    public LoginTetheredUser toLoginTetheredUserRequest(String guid, Scheme scheme) {
        LoginTetheredUserRequestType request = new LoginTetheredUserRequestType();
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        request.setTetheredUserGuid("{" + guid + "}");

        LoginTetheredUser loginTetheredUser = new LoginTetheredUser();
        loginTetheredUser.setRequest(request);
        return loginTetheredUser;
    }
    
    
    @Override
    public PibaTetheredGuidRequest toPibaTetheredGuidRequest(String tetherGuid, EmployeeDetails employeeDetails,
                                                             Scheme scheme) {
        return new PibaTetheredGuidRequest(Integer.parseInt(employeeDetails.getCompanyId()),
                Integer.parseInt(employeeDetails.getEmployeeId()), tetherGuid, scheme);
    }

    private HeaderType getWorldLineRequestHeader(Scheme scheme) {
        HeaderType header = Scheme.DE.equals(scheme) ? getDeHeader()  : getGbHeader();
        header.setClientMessageId(String.valueOf(UUID.randomUUID()));
        log.debug("Adding ClientMessageId={}", header.getClientMessageId());
        return header;
    }

    private TrustedPartnerCredentialsType getWorldLineCredentialsType(Scheme scheme) {
        return Scheme.DE.equals(scheme) ? getDeCredentials() : getGbCredentials();
    }
}
