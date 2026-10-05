package uk.co.whitbread.piba.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.whitbread.piba.account.config.PibaGuidErrorDecodeConfig;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;

import java.util.List;


@FeignClient(value = "${feign.pibaguid.name:pibaguid}", url = "${feign.pibaguid.url}",
        configuration = PibaGuidErrorDecodeConfig.class,
        fallbackFactory = PibaGuidClientFallbackFactory.class)
public interface PibaGuidClient {
    @GetMapping(value = "/piba/guid/{companyId}/{employeeId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    PibaTetheredGuidResponse getGuids(@PathVariable("companyId") String companyId, @PathVariable("employeeId") String employeeId);

    @GetMapping(value = "/piba/getMultiTetheredGuids/{companyId}/{employeeId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    List<PibaTetheredGuidResponse> getMultiGuids(@PathVariable("companyId") String companyId, @PathVariable("employeeId") String employeeId);
}
