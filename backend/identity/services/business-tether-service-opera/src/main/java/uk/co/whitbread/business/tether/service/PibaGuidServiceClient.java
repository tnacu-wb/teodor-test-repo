package uk.co.whitbread.business.tether.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.business.tether.client.PibaGuidClient;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidRequest;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidResponse;
import uk.co.whitbread.business.tether.validation.PibaGuidValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class PibaGuidServiceClient {
	
	private final PibaGuidValidator pibaGuidValidator;
	private final PibaGuidClient pibaGuidClient;
	
	
	public PibaTetheredGuidResponse savePibaGuid(PibaTetheredGuidRequest pibaTetheredGuidRequest) {
		log.info("Making request to save tether guid for company id {}.", pibaTetheredGuidRequest.getCompanyId());
		var pibaTetheredGuidResponse = pibaGuidClient.saveGuid(pibaTetheredGuidRequest);
		pibaGuidValidator.validate(pibaTetheredGuidResponse);
		log.info("Tether guid is created for companyId {}.", pibaTetheredGuidResponse.getTetheredGuid());
		return pibaTetheredGuidResponse;
	}
	
}
