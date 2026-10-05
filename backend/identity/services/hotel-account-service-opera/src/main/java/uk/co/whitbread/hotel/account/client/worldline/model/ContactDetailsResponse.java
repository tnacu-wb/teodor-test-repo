package uk.co.whitbread.hotel.account.client.worldline.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
public class ContactDetailsResponse {

	@JsonProperty("Errors")
	private List<ErrorsItem> errors;

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("Data")
	private String data;
}