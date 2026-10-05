package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = "paymentCard")
public class Payment {

	@Valid
	@NotNull
	@Schema(required = true)
	private Booker booker;
	@Valid
	@Size(min = 1)
	@NotNull
	@Schema(required = true)
	private List<Guest> guests;
	@Valid
	@NotNull
	@Schema(required = true)
	private PaymentCard paymentCard;
	@NotNull
	@Schema(required = true)
	private Price donation;
	@Valid
	private List<Breakfast> breakfasts;
	@Valid
	private List<UpsellItem> upsellItems;

	@Deprecated
	private Boolean isBusinessTrip;

	private Boolean business;

	private String cbtSessionId;

}
