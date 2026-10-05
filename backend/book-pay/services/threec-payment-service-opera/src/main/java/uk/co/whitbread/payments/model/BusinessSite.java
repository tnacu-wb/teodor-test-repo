package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.payments.validation.ValueOfEnum;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessSite {

    @NotEmpty
    @Schema(required = true, description = "The unique site identifier.", example = "LONHOL")
    private String identifier;

    @Schema(description = "The name of the site.", example = "London Holborn Premier Inn")
    private String name;

    @NotEmpty
    @ValueOfEnum(enumClass = BusinessType.class)
    @Schema(description = "Type of business site.",
            implementation = BusinessType.class,
            example = "HOTEL")
    private String type;

    @Schema(description = "The city/town that the business site is in.", example = "London")
    private String location;

    @Schema(description = "The country that the business site is in.", example = "GB")
    private String country;

    @ArraySchema(schema = @Schema(example = "wifi", description = "List of additional options included as part of the booking.. e.g breakfast, wifi"))
    private List<String> additionalServices;

}
