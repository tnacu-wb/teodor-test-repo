package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room {

    @Schema(example ="FAM", description = "Type of room being booked.")
    private String type;

    @Schema(example ="SV344", description = "Rate type for the room being booked.")
    private String rate;

    @Schema(example ="2", description = "Number of adults in room being booked.")
    private int adults;

}
