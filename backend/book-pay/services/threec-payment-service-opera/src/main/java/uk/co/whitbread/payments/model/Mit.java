package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@Builder
public class Mit {
    @Schema(description = "scaReference", example = "43686363637")
    private String scaReference;

    @Schema(description = "type", example = "N")
    private MitType type;
}
