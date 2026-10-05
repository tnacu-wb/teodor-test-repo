package uk.co.whitbread.hotel.account.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class ErrorsItem{

	@JsonProperty("Target")
	private String target;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("Code")
	private String code;
}