package uk.co.whitbread.hotel.card.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

class HotelCardWebClientConfigTest {

  @Test
  void shouldDeserializeSharedCdhResponseWhenPrimitiveBooleanIsNull() throws Exception {
    String jsonWithNullPrimitiveBoolean = """
        {
          "employeeAccountId": "EMPL_123",
          "companyAccountId": "COMP_123",
          "bookingPreference": {
            "preselectWifi": null
          }
        }
        """;

    JsonMapper configuredMapper = new JacksonConfig().jsonMapper();

    GetEmployeeResponse response = configuredMapper.readValue(
        jsonWithNullPrimitiveBoolean, GetEmployeeResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getEmployeeAccountId()).isEqualTo("EMPL_123");
    assertThat(response.getCompanyAccountId()).isEqualTo("COMP_123");
    assertThat(response.getBookingPreference()).isNotNull();
    assertThat(response.getBookingPreference().isPreselectWifi()).isFalse();
  }

  @Test
  void shouldFailWithDefaultJsonMapperWhenPrimitiveBooleanIsNull() {
    String jsonWithNullPrimitiveBoolean = """
        {
          "employeeAccountId": "EMPL_123",
          "companyAccountId": "COMP_123",
          "bookingPreference": {
            "preselectWifi": null
          }
        }
        """;

    JsonMapper defaultMapper = JsonMapper.builder().build();

    assertThat(defaultMapper.deserializationConfig()
        .isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)).isTrue();

    assertThatThrownBy(() -> defaultMapper.readValue(
        jsonWithNullPrimitiveBoolean, GetEmployeeResponse.class))
        .hasMessageContaining("Cannot map `null` into type `boolean`");
  }
}
