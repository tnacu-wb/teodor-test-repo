package uk.co.whitbread.business.tether.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PibaGuidClientFallbackFactory implements FallbackFactory<PibaGuidClient> {
    
    @Override
    public PibaGuidClient create(Throwable throwable) {
        return tetheredGuidRequest -> {
            log.warn("Failed to call Pibaguid API when saving piba guid for tetherguid={}, returning fallback. {}", tetheredGuidRequest.getTetheredGuid(), throwable);
            return null;
        };
        
    }
}
