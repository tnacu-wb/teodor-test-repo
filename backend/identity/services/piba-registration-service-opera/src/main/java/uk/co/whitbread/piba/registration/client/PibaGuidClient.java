package uk.co.whitbread.piba.registration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.piba.registration.config.PibaGuidErrorDecodeConfig;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidRequest;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidResponse;

@FeignClient(value = "${feign.pibaguid.name}", url = "${feign.pibaguid.url}", configuration = PibaGuidErrorDecodeConfig.class,
		fallbackFactory = PibaGuidClientFallbackFactory.class)
public interface PibaGuidClient {
	
	@PostMapping(value = "/piba/guid",
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	PibaTetheredGuidResponse saveGuid(@Validated @RequestBody
			PibaTetheredGuidRequest request);
	
}
