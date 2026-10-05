package uk.co.whitbread.hotel.account.feign;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.HotelAccountMicroserviceApplication;
import uk.co.whitbread.hotel.account.client.pibaguid.PibaGuidClient;
import uk.co.whitbread.hotel.account.config.TestWireMockServer;
import uk.co.whitbread.hotel.account.model.PibaTetheredGuidResponse;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelAccountMicroserviceApplication.class)
@TestPropertySource(properties = {
		"feign.pibaguid.name:pibaguid",
		"feign.pibaguid.url:localhost:${wiremock.server.port}",
		"spring.cloud.openfeign.lazy-attributes-resolution=true"
})
public class PibaGuidClientTestIT {

	static {
		TestWireMockServer.start();
	}
	
	@Autowired
	private PibaGuidClient pibaGuidClient;
	
	@Test
	public void shouldVerifyPibaGuidFeignClient() {
		PibaTetheredGuidResponse pibaTetheredGuidResponse = pibaGuidClient.getGuids("123","55");
		
		assertThat(pibaTetheredGuidResponse.getTetheredGuid(), is(equalTo(of("CE3C22D3-D413-4FB2-8219-1160FEE0FC21"))));
	}
}