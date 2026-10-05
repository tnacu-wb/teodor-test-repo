package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class EckohResponse {

    @JsonProperty(value = "content")
    private String iFrame;
    private String result;
    private long resultCode;
    private String maskedPan;
    private String expiry;
    private String scheme;
    private String type;
    private String reference;
    private String token;
    private EckohException exception;
}
