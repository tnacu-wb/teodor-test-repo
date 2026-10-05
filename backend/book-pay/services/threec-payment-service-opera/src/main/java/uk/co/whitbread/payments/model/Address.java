package uk.co.whitbread.payments.model;

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
public class Address {

    @Schema(description = "Address line 1",
            example = "Whitbread Group PLC")
    private String line1;

    @Schema(description = "Address line 2",
            example = "Houghton Hall Business Park")
    private String line2;

    @Schema(description = "Address line 3",
            example = "Porz Avenue")
    private String line3;

    @Schema(description = "Address line 4",
            example = "Dunstable")
    private String line4;

    @Schema(description = "Address line 4",
        example = "")
    private String line5;


    @Schema(description = "Country Code",
            example = "GB",
            defaultValue = "GB")
    @Builder.Default
    private String countryCode = "GB";

    @Schema(description = "Postal code",
            example = "LU5 5XE")
    private String postalCode;

    @Schema(description = "Company name",
        example = "Whitbread")
    private String companyName;

    @Schema(description = "Address type",
        example = "Whitbread")
    private String type;

}
