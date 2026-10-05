package uk.co.whitbread.piba.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.piba.registration.client.PibaGuidClient;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidRequest;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.registration.validation.PibaGuidValidator;

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
