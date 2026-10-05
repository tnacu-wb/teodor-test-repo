package uk.co.whitbread.hotel.account.client.pibaguid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.hotel.account.model.PibaTetheredGuidResponse;

import java.util.List;


@FeignClient(value = "${feign.pibaguid.name:pibaguid}", url = "${feign.pibaguid.url}",
        fallbackFactory = PibaGuidClientFallbackFactory.class)
public interface PibaGuidClient {

    @RequestMapping(value = "/piba/guid/{companyId}/{employeeId}",
            method = RequestMethod.GET,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    PibaTetheredGuidResponse getGuids(@PathVariable("companyId") String companyId, @PathVariable("employeeId") String employeeId);

    @RequestMapping(value = "/piba/getMultiTetheredGuids/{companyId}/{employeeId}",
            method = RequestMethod.GET,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    List<PibaTetheredGuidResponse> getMultiGuids(@PathVariable("companyId") String companyId, @PathVariable("employeeId") String employeeId);

}