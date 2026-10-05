package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Guest {

    @Schema(description = "Full name of guest.", example = "Mr James Bond")
    private String name;

    @Schema(description = "Whether the guest is registered with a Premier Inn account.", example = "true")
    private boolean registered;

    @Schema(description = "How long the guest has been registered with a Premier Inn Account.", example = "2021-11-26")
    private LocalDate registeredSince;

    @Schema(description = "How many previous bookings the customer has made.", example = "10")
    private int previousBookings;

}
