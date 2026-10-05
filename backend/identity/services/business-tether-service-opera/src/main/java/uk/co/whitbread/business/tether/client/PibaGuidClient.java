package uk.co.whitbread.business.tether.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.business.tether.config.PibaGuidErrorDecodeConfig;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidRequest;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidResponse;

@FeignClient(value = "${feign.pibaguid.name:pibaguid}", url = "${feign.pibaguid.url}",
		configuration = PibaGuidErrorDecodeConfig.class,
		fallbackFactory = PibaGuidClientFallbackFactory.class)
public interface PibaGuidClient {
	
	@RequestMapping(value = "/piba/guid",
			method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	PibaTetheredGuidResponse saveGuid(@Validated @RequestBody PibaTetheredGuidRequest request);
	
}
