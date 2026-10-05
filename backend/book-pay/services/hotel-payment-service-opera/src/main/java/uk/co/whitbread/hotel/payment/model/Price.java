package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Price {
	@NotNull
	@Schema(required = true, example = "150.45")
	private Double amount;
	@NotEmpty
	@Schema(required = true, example = "GBP")
	private String currency;
}
