package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.payments.validation.ValueOfEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {

    @NotEmpty
    @ValueOfEnum(enumClass = ChannelType.class)
    @Schema(description = "Type of booking channel that the payment is for.",
            implementation = ChannelType.class,
            example = "PI")
    private String channel;

    @Schema(description = "Any further more specific information on booking channel. Such as specific GDS partner.",
            example = "Amadeus",
            hidden = true)
    private String subChannel;

    @NotEmpty
    @ValueOfEnum(enumClass = JourneyType.class)
    @Schema(description = "Type of customer journey the payment is for.",
            implementation = JourneyType.class,
            example = "BOOKING")
    private String journey;

    @NotEmpty
    @ValueOfEnum(enumClass = BookingType.class)
    @Schema(description = "Type of booking the payment is for.",
            implementation = BookingType.class,
            example = "PAY_NOW")
    private String type;

    @Schema(description = "Booking reference.", example ="BR260692A or Opera UUID" )
    private String reference;

    @NotNull
    @Schema(description = "Information on the site the booking is for. A site can be a hotel, restaurant or another entity run by Whitbread.")
    @Valid
    private BusinessSite businessSite;

    @Schema(description = "Arrival date of the booking.", example ="2021-11-25" )
    private LocalDate arrivalDate;

    @Schema(description = "Departure date of the booking.", example = "2021-11-26")
    private LocalDate departureDate;

    @ValueOfEnum(enumClass = Language.class)
    @Schema(description = "Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.",
            implementation = Language.class,
            example = "en")
    private String language;

    @Schema(description = "Information regarding the lead guest of the booking.")
    private Guest leadGuest;

    @Schema(description = "Information regarding the agent that does the booking on behalf of the customer.")
    private Agent agent;

    @Schema(description = "Information on the rooms being booked.")
    private List<Room> rooms;

    @Schema(description = "Opera Booking reference.", example ="Opera non uuid basket ref GAA12345" )
    private String bookingReference;

}
