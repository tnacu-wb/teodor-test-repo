package uk.co.whitbread.piba.account.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import java.util.List;

@Slf4j
@Component
public class PibaGuidClientFallbackFactory implements FallbackFactory<PibaGuidClient> {

    @Override
    public PibaGuidClient create(final Throwable throwable) {
        return new PibaGuidClient() {
            @Override
            public PibaTetheredGuidResponse getGuids(String companyId, String employeeId) {
                log.warn("Failed to call Pibaguid API when get piba guids for company={}, employee={} returning fallback. {}", companyId, employeeId, throwable);
                return null;
            }

            @Override
            public List<PibaTetheredGuidResponse> getMultiGuids(String companyId, String employeeId) {
                log.warn("Failed to call Piba Multi guid API when get piba guids for company={}, employee={} returning fallback. {}", companyId, employeeId, throwable);
                return null;
            }
        };
    }
}
