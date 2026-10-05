package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CdhReservationSearchClientV1Test {

  @Autowired
  private CdhReservationSearchClientV1 cdhReservationSearchClientV1;
  @MockitoBean
  private CustomerDataHubClient cdhClient;
  @Autowired
  private JsonMapper jsonMapper;

  @Test
  void verifyGetReservationById() {

    when(cdhClient.getCdh(anyString(), eq(ReservationSearch.class)))
            .thenReturn(buildReservationSearchResponse());

    var response = cdhReservationSearchClientV1.getReservationById("AEWR278034");

    assertNotNull(response);
    assertEquals(buildReservationSearchResponse(), response);
  }

  public ReservationSearch buildReservationSearchResponse() {

    return jsonMapper.readValue(
            new File("src/test/resources/samples/get_reservation_search_response.json"),
            ReservationSearch.class);
  }
}
