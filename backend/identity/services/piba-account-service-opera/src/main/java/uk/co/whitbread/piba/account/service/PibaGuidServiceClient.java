package uk.co.whitbread.piba.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.piba.account.client.PibaGuidClient;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.validation.PibaGuidValidator;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PibaGuidServiceClient {
	
	private final PibaGuidValidator pibaGuidValidator;
	private final PibaGuidClient pibaGuidClient;

	public PibaTetheredGuidResponse getGuids(String companyId, String employeeId) {
		log.info("Retrieve guids request for company id {}, employee id {}", companyId, employeeId);
		var result = pibaGuidClient.getGuids(companyId, employeeId);
		pibaGuidValidator.validate(result);
		return result;
	}

	 List<PibaTetheredGuidResponse> getMultiGuids(String companyId, String employeeId) {
		log.info("Retrieve MULTI guids request for company id {}, employee id {}", companyId, employeeId);
		var result = pibaGuidClient.getMultiGuids(companyId, employeeId);
		pibaGuidValidator.validateMultiple(result);
		return result;
	}
}
