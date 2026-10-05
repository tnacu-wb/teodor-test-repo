package uk.co.whitbread.business.tether.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class SchemeExtractor {

    public Scheme extractScheme(TetherLinkRequest tetherLinkRequest) {
        Scheme scheme = EnumUtils.getEnum(Scheme.class, parseCountryCode(tetherLinkRequest.getLinkCode()));
        if (isNull(scheme)) {
            log.warn("Scheme extraction failed. Reverting to default: GB");
            return Scheme.GB;
        }
        return scheme;
    }

    private String parseCountryCode(String linkCode) {
        return capitalise(linkCode.length() > 2 ? linkCode.substring(linkCode.length() - 2) : linkCode);
    }

    private String capitalise(String countryCode) {
        return countryCode.toUpperCase();
    }
}
