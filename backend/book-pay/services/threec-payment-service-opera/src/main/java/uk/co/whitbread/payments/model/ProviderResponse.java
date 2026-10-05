package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ProviderResponse {
    private String providerReference;
    private String transactionReference;
    @JsonProperty(value = "threecResponse")
    private ThreeCResponse threeCResponse;
    private EckohResponse eckohResponse;
}
